from sklearn.feature_extraction.text import CountVectorizer,TfidfVectorizer
import pandas as pd

texts=["orange banana apple grape","banana apple apple","grape", 'orange apple'] 
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
