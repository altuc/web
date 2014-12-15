import sys
import re
from math import log

K = {}  # dict to store the K value for each document
f = {}  # dict to store the term frequencies in inverted list
n = {}  # dict to store the occurence in how many documents for each word
q = {}  # dict to store the result
k1 = 1.2
b = 0.75
k2 = 100.0
r = 0.0
R = 0.0
qf = 1.0
N = 0.0

# this function is used to process index.out
def processindex(indexdir):
	global k1
	global b
	global N
	totaldl = 0.0
	dl = {}
	fobj = open(indexdir, "r")
	for line in fobj:
		if '#' in line:
			strs = line.rstrip()
			splits = strs.split()
			dl[splits[1]] = splits[2]
			totaldl += float(splits[2])
			N += 1
		else:
			strs = line.rstrip()
			splits = strs.split(" -> ")
			p = re.compile(r'\(\d+\,\s\d+\)')
			lst = p.findall(splits[1])
			n[splits[0]] = len(lst)
			f[splits[0]] = {}
			for pair in lst:
				num = pair.split(", ")
				f[splits[0]][num[0][1:]] = num[1][:len(num[1]) - 1]
	avdl = totaldl / N
	for key in dl:
		K[key] = k1 * ((1 - b) + b * float(dl[key]) / avdl)
	fobj.close()

# this function is used to process queries.txt
def processquery(queriesdir):
	global k1
	global k2
	global r
	global R
	global qf
	global N
	fobj = open(queriesdir, "r")
	i = 1
	for line in fobj:
		strs = line.rstrip()
		words = strs.split()
		bm25 = {}
		for key in K:
			bm25[key] = 0.0
			for word in words:
				if key in f[word]:
					bm25[key] += log((r + 0.5) / (R - r + 0.5) / ((float(n[word]) - r + 0.5) / (N - float(n[word]) - R + r + 0.5))) * ((k1 + 1) * float(f[word][key]) / (K[key] + float(f[word][key]))) * ((k2 + 1) * qf / (k2 + qf))
				else:
					bm25[key] += 0.0
		q[i] = bm25
		i += 1
	fobj.close()

# this function is used to output the result
def output(itrs):
	sortedq = sorted(q.items(), key=lambda d:d[0])
	for tup in sortedq:
		sortedbm25 = sorted(tup[1].items(), key=lambda d:d[1], reverse=True)
		i = 1
		for subtup in sortedbm25:
			print str(tup[0]) + " Q0 " + str(subtup[0]) + " " + str(i) + " " + str(subtup[1]) + " zhouhanjiang"
			if i == int(itrs):
				break
			else:
				i += 1

# this main function is used to run all the functions above
def main():
	processindex(sys.argv[1])
	processquery(sys.argv[2])
	output(sys.argv[3])

if __name__ == "__main__":
	main()
