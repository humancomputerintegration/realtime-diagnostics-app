#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Jun  5 20:56:09 2019

@author: hanxinzhang
"""


from gensim.models import Word2Vec
from gensim.models.word2vec import LineSentence

sentenceCorpus = LineSentence('full_sentence_corpus/full_sentence_corpus.txt')

model = Word2Vec(sentences=sentenceCorpus, 
                 window=100,
                 seed=2019,
                 workers=16)

model.save('word2vec models/word2vec_bmc.model')