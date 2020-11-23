import native stdio;
native def printf(format : Ref[Char], arguments : Any...) : Void;
native type file : FILE;
native def fopen(name : Ref[Char], mode : Ref[Char]) : Ref[FILE];
native def fclose(file : Ref[FILE]);
native def fgetc(file : Ref[FILE]) : I16;

import native stdlib;
native def malloc(size : Size) : Ref[Any];
native def free(block : Ref[Any]) : Void;

import native errno;
native const errno : I16;

import native string;
native def strerror(errorNumber : I16) => Ref[Char];

struct Option {
    const value : Ref[Any];
    const isPresent : (Ref[Option]) => Bool;
    const get : (Ref[Option]) => Ref[Any];
}

def isPresent_Some(this : Ref[Option]) : Bool => {
    return true;
}

def get_Some(this : Ref[Option]) : Ref[Any] => {
    return *(this).value;
}

def Some(value : Ref[Any]) : Option => {
    const this = [Option]{
        value,
        isPresent_Some,
        get_Some
    };
    return this;
}

def isPresent_None(this : Ref[Option]) : Bool => {
    return false;
}

def get_None(this : Ref[Option]) : Ref[Any] => {
    return null;
}

def None() : Option => {
    const this : Option = [Option]{
        null,
        isPresent_None,
        get_None
    }
    return this;
}

struct AllocatorNode {
    const block : Ref[Any];
    let next : Ref[AllocatorNode];

    const delete : (Ref[AllocatorNode]) => Void;
}

def delete_AllocatorNode(this : Ref[AllocatorNode]) : Void => {
    const block : Ref[Any] = this -> block;
    if(block){
        free(block);
    }
    const next = Ref[AllocatorNode] = this -> next;
    if(next){
        next -> delete(next);
    }
    free(this);
}

def AllocatorNode_(const bufferSize : Size) : Ref[AllocatorNode] => {
    const this : Ref[AllocatorNode] = malloc(AllocatorNode.size);
    this -> delete = delete_AllocatorNode;
    this -> block = malloc(bufferSize);
    return this;
}

struct Allocator {
    let head : Ref[AllocatorNode];

    const last : (Ref[Allocator]) : Option;
    const allocator : (Ref[Allocator], Size) : Ref[Any];
    const clear : (Ref[Allocator]) : Ref[Allocator];
}

def last_Allocator(this : Ref[Allocator]) => {
    const head : Ref[AllocatorNode] = *this.head;
    if(!head) {
        return None();
    } else {
        let current : Ref[AllocatorNode] = head;
        let next : Ref[AllocatorNode] = *current.next;
        while(next){
            current = next;
            next = *current.next;
        }
        return Some(current);
    }
}

def allocate_Allocator(this : Ref[Allocator], bufferSize : Size) : Ref[Any] => {
    const node : Ref[AllocatorNode] = AllocatorNode_(bufferSize);
    const lastOption = this -> last(this);
    if(lastOption.isPresent(&lastOption)){
        const last : Ref[AllocatorNode] = lastOption.get(&lastOption);
        last -> next = node;
    } else {
        this -> head = node;
    }
    return node -> block;
}

def clear_Allocator(this : Ref[Allocator]) : Ref[Allocator] => {
    const head : Ref[AllocatorNode] = this -> head;
    (*head).delete(head);
    return this;
}

def Allocator_() : Allocator => {
    const this : Allocator = [Allocator]{
        head,
        last_Allocator,
        allocate_Allocator,
        clear_Allocator
    };
    return this;
}

def main() : I16 => {
    const file : Ref[FILE] = fopen("Main.mg", "r");
    if(file){
        fclose(file);
    } else {
        printf("Failed to open Main.mg: %s", strerror(errno));
    }
    return 0;
}