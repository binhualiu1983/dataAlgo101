import matplotlib.pyplot as plt
import pandas as pd
import string
import codecs
import os
import jieba
from wordcloud import WordCloud
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.model_selection import train_test_split
import requests
import openpyxl
import numpy as np



#https://blog.csdn.net/weixin_40283570/article/details/97111691 python环境
#https://www.runoob.com/numpy/numpy-install.html numpy等安装
# https://www.cnblogs.com/baxianhua/p/10701778.html pandas
# https://blog.csdn.net/qq_33335484/article/details/82288351 贝叶斯分类中文垃圾邮件
# https://www.zhihu.com/question/388927818 pandas矩阵全加1
# https://www.cnblogs.com/morvan/p/11170769.html pandas创建矩阵


#transform text to sparse matrix
def transformTextToSparseMatrix(texts):
    vectorizer = CountVectorizer(binary=False)
    vectorizer.fit(texts)
 
    #inspect vocabulary
    vocabulary = vectorizer.vocabulary_
    print("There are ", len(vocabulary), " word features")
    
    vector = vectorizer.transform(texts)
    result = pd.DataFrame(vector.toarray())
    
    keys = []
    values = []
    for key,value in vectorizer.vocabulary_.items():
        keys.append(key)
        values.append(value)
    df = pd.DataFrame(data = {"key" : keys, "value" : values})
    colnames = df.sort_values("value")["key"].values
    result.columns = colnames
    return result


def showWordCloud(text):
    wc = WordCloud(
    background_color="white",
    max_words=100,
    font_path="c:/Code/code/dataAlgo101/数据集/中文广告数据集/simhei.ttf",
    min_font_size=10,
    max_font_size=60, 
    width=1600,
    height=1600,
     scale=4
    )
    wordcloud = wc.generate(text)
    plt.imsave("c:/Code/ham.png",wordcloud)
    #plt.imshow(wordcloud, interpolation="bilinear")
    #waitKey(0)

stopwords = codecs.open("c:/Code/code/dataAlgo101/数据集/stopwords/baidu_stopwords.txt", 'r', 'UTF8').read().split('\n')


spam_dataframe = pd.read_csv("c:/Code/code/dataAlgo101/数据集/中文广告数据集/spam_5000.utf8", sep='\n', header=None, names=["text", "no"], nrows=4000)
print(spam_dataframe)

processed_texts = []
for text in spam_dataframe["text"]:
    words = []
    seg_list = jieba.cut(text)
    for seg in seg_list:
        if(seg.isalpha()) & (seg not in stopwords):
            words.append(seg)
    sentence = " ".join(words)
    processed_texts.append(sentence)
spam_dataframe["text"] = processed_texts

spam_textmatrix = transformTextToSparseMatrix(spam_dataframe["text"])
#pop freq words
features = pd.DataFrame(spam_textmatrix.apply(sum, axis=0))
spam_extractedfeatures = [features.index[i] for i in range(features.shape[0]) if features.iloc[i, 0] > 15]
spam_textmatrix = spam_textmatrix[spam_extractedfeatures]
print("There are ", spam_textmatrix.shape[1], " word features")
spam_textmatrix = spam_textmatrix.sum(axis = 0)
spam_textmatrix = spam_textmatrix +1
spam_textmatrix = np.log(spam_textmatrix)
#spam_textmatrix = spam_textmatrix.dot(np.ones(spam_textmatrix.shape[1]))
#df = pd.DataFrame(np.ones(spam_textmatrix.shape[0]))
#spam_textmatrix = df.dot(spam_textmatrix)
#spam_textmatrix = spam_textmatrix.dot(np.ones(spam_textmatrix.shape[0]))
spam_textmatrix.to_excel(r'c:\Code\spam.xlsx')



ham_dataframe = pd.read_csv("c:/Code/code/dataAlgo101/数据集/中文广告数据集/ham_5000.utf8", sep='\n', header=None, names=["text", "no"], nrows=4000)
print(ham_dataframe)


processed_texts = []
for text in ham_dataframe["text"]:
    words = []
    seg_list = jieba.cut(text)
    for seg in seg_list:
        if(seg.isalpha()) & (seg not in stopwords):
            words.append(seg)
    sentence = " ".join(words)
    processed_texts.append(sentence)
ham_dataframe["text"] = processed_texts
#showWordCloud(" ".join(spam_dataframe["text"]))

ham_textmatrix = transformTextToSparseMatrix(ham_dataframe["text"])
#pop freq words
features = pd.DataFrame(ham_textmatrix.apply(sum, axis=0))
ham_extractedfeatures = [features.index[i] for i in range(features.shape[0]) if features.iloc[i, 0] > 15]
ham_textmatrix = ham_textmatrix[ham_extractedfeatures]
print("There are ", ham_textmatrix.shape[1], " word features")
ham_textmatrix = ham_textmatrix.sum(axis = 0)
ham_textmatrix = ham_textmatrix +1
ham_textmatrix = np.log(ham_textmatrix)
ham_textmatrix.to_excel(r'c:\Code\ham.xlsx')

spam_dataframe = pd.read_csv("c:/Code/code/dataAlgo101/数据集/中文广告数据集/spam_5000.utf8", sep='\n', header=None, names=["text", "no"], skiprows=4000)
print(spam_dataframe)

processed_texts = []


total = 0
error = 0
for text in spam_dataframe["text"]:
    words = []
    seg_list = jieba.cut(text)
    for seg in seg_list:
        if(seg.isalpha()) & (seg not in stopwords):
            words.append(seg)
    
    ham_posibility = 0.0
    spam_posibility = 0.0
    for word in words:
        try:
            item = ham_textmatrix.at[word]
            ham_posibility += item
        except KeyError:
            ham_posibility += 0.0

        try:
            item = spam_textmatrix.at[word]
            spam_posibility += item
        except KeyError:
            spam_posibility += 0
    
    total+=1
    if(ham_posibility>spam_posibility):
        print("正常邮件")
        error+=1
    else:
        print("垃圾邮件")

print(total)
print(error)





ham_dataframe = pd.read_csv("c:/Code/code/dataAlgo101/数据集/中文广告数据集/ham_5000.utf8", sep='\n', header=None, names=["text", "no"], skiprows=4000)
print(ham_dataframe)

processed_texts = []
total = 0
error = 0
for text in ham_dataframe["text"]:
    words = []
    seg_list = jieba.cut(text)
    for seg in seg_list:
        if(seg.isalpha()) & (seg not in stopwords):
            words.append(seg)
    
    ham_posibility = 0.0
    spam_posibility = 0.0
    for word in words:
        try:
            item = ham_textmatrix.at[word]
            ham_posibility += item
        except KeyError:
            ham_posibility += 0.0

        try:
            item = spam_textmatrix.at[word]
            spam_posibility += item
        except KeyError:
            spam_posibility += 0
    
    total+=1
    if(ham_posibility>spam_posibility):
        print("正常邮件")
    else:
        print("垃圾邮件")
        error+=1

print(total)
print(error)

