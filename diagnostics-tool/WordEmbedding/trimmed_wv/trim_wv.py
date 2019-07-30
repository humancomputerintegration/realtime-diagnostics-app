#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Jun 27 11:46:59 2019

@author: hanxinzhang
"""

from gensim.models import KeyedVectors
from gensim.models import Word2Vec
import numpy as np


def restrict_w2v(w2v, restricted_word_set):
    
    new_vectors = []
    new_vocab = {}
    new_index2entity = []

    for i in range(len(w2v.vocab)):
        
        word = w2v.index2entity[i]
        
        if word in restricted_word_set:
            
            vec = w2v.vectors[i]
            vocab = w2v.vocab[word]
            
            vocab.index = len(new_index2entity)
            new_index2entity.append(word)
            new_vocab[word] = vocab
            new_vectors.append(vec)
            
        if i % 10000 == 0:
            print(i)
        
    w2v.vocab = new_vocab
    w2v.vectors = np.vstack(new_vectors)
    w2v.index2entity = new_index2entity
    w2v.index2word = new_index2entity
    # w2v.vectors_norm = None
    

model = Word2Vec.load('word2vec_full.model')
word_vectors = model.wv

sel_word = []

for word, vocab in word_vectors.vocab.items():
    
    if ('UMLS_' in word) and (len(word) == 13):
        sel_word.append(word)
    
    if vocab.count >= 200:
        sel_word.append(word)

sel_word = set(sel_word)

restrict_w2v(word_vectors, sel_word)
word_vectors.save('trimmed_wv.kv')

# Test ------------------------------------------------------------------------

wv2 = KeyedVectors.load('trimmed_wv.kv', mmap='r')
