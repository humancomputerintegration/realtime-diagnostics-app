#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Fri Jun  7 08:44:27 2019

@author: hanxinzhang
"""

import pandas as pd
import numpy as np
from gensim.models import Word2Vec

disSympMapping = pd.read_csv('Dis_Sym_Matrix.csv', index_col=0)
sympNames = disSympMapping.columns

model = Word2Vec.load('word2vec models/word2vec_bmc.model')


weightMatrix = disSympMapping.copy()

for idx, row in weightMatrix.iterrows():
    
    for symp in sympNames:
        
        if row[symp] > 0.:
            
            try:
                
                cos_sim = model.wv.similarity('UMLS_' + idx, 'UMLS_' + symp)
                
            except KeyError:
                
                pass
            
            else:
                
                ang_sim = 1. - np.arccos(cos_sim) / np.pi   
                row[symp] = ang_sim

weightMatrix = weightMatrix.apply(lambda x:x**3)
weightMatrix = weightMatrix.div(weightMatrix.sum(axis=1), axis=0)

weightMatrix.to_csv('Dis_Sym_corpus.csv')

