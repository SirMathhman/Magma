//
// Created by mathm on 11/23/2020.
//

#include "Main.h"
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

typedef char I8;
typedef int I16;
typedef void Any;
typedef int Bool;
typedef size_t Size;
typedef void Void;

#define false 0
#define true 1

typedef struct Option {
    Any *value;

    Bool (*isPresent)(struct Option *);

    Any *(*get)(struct Option *);
} Option;

Bool isPresent_Some(Option *this) {
    return true;
}

Any *get_Some(Option *this) {
    return this->value;
}

Option Some(Any *value) {
    Option this;
    this.isPresent = isPresent_Some;
    this.get = get_Some;
    this.value = value;
    return this;
}

Bool isPresent_None(Option *this) {
    return false;
}

Any *get_None(Option *this) {
    return 0;
}

Option None() {
    Option this;
    this.isPresent = isPresent_None;
    this.get = get_None;
    return this;
}

typedef struct AllocatorNode {
    Any *block;
    struct AllocatorNode *next;

    Void (*delete)(struct AllocatorNode *);
} AllocatorNode;

void delete_AllocatorNode(AllocatorNode *this) {
    Any *block = this->block;
    if (block) {
        free(block);
    }
    AllocatorNode *next = this->next;
    if (next) {
        next->delete(next);
    }
    free(this);
}

AllocatorNode *AllocatorNode_(Size size) {
    AllocatorNode *this = malloc(sizeof(AllocatorNode));
    this->delete = delete_AllocatorNode;
    this->block = malloc(size);
    return this;
}

typedef struct Allocator {
    AllocatorNode *head;

    Option (*last)(struct Allocator *);

    Any *(*allocate)(struct Allocator *, Size);

    struct Allocator *(*clear)(struct Allocator *);
} Allocator;

Option last_Allocator(Allocator *this) {
    AllocatorNode *head = this->head;
    if (!head) return None();
    else {
        AllocatorNode *current = head;
        AllocatorNode *next = current->next;
        while (next) {
            current = next;
            next = current->next;
        }
        return Some(current);
    }
}

Any *allocate_Allocator(Allocator *this, Size size) {
    AllocatorNode *node = AllocatorNode_(size);
    Option lastOption = last_Allocator(this);
    if (lastOption.isPresent(&lastOption)) {
        AllocatorNode *last = lastOption.get(&lastOption);
        last->next = node;
    } else {
        this->head = node;
    }
    return node->block;
}

Allocator *clear_Allocator(Allocator *this) {
    AllocatorNode *head = this->head;
    head->delete(head);
    return this;
}

Allocator Allocator_() {
    Allocator this;
    this.allocate = allocate_Allocator;
    this.clear = clear_Allocator;
    this.last = last_Allocator;
    return this;
}

int main() {
    FILE *file = fopen("Main.mg", "r");
    if (file) {
        fseek(file, 0, SEEK_END);
        long size = ftell(file);
        rewind(file);
        fclose(file);

        Allocator allocator = Allocator_();
        I8 *block = allocator.allocate(&allocator, size);
        for (int i = 0;; i++) {
            char c = fgetc(file);
            if (c == EOF) {
                break;
            } else {
                block[i] = c;
            }
        }
        allocator.clear(&allocator);
    } else {
        printf("Failed to open Main.mg: %s", strerror(errno));
    }
    return 0;
}
