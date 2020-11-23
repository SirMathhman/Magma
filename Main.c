//
// Created by mathm on 11/23/2020.
//


#include "Main.h"
#include "Core.h"
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

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
    Option lastOption = this->last(this);
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
    (*head).delete(head);
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
        I16 *block = allocator.allocate(&allocator, size * sizeof(I16));
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
