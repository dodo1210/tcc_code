arq = open('text.txt','r')
string = arq.readlines()
arq.close

for s in string:
    i=0
    cont =0 
    for i in range(len(s)):
        if s[i] == '0':
            cont+=1
    print(cont)
