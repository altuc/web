import sys

invertedindex = {}  # dict to store term frequencies (tf) in inverted lists: word -> (docid, tf), (docid, tf), ...
doclength = {}  # dict to store the number of tokens in each document

# this function is used to get the term frequencies in inverted lists for the input document
def read(inputdir):
	doclen = 0
	fobj = open(inputdir, "r")
	for line in fobj:
		strs = line.rstrip()
		words = strs.split()
		if '#' in line:
			docid = int(words[1])
			if docid > 1: 
				doclength[docid - 1] = doclen
				doclen = 0
		else:
			for word in words:
				doclen += 1
				if word in invertedindex:
					if docid in invertedindex[word]:
						invertedindex[word][docid] += 1
					else:
						invertedindex[word][docid] = 1
				else:
					invertedindex[word] = {}
					invertedindex[word][docid] = 1
	doclength[docid] = doclen
	fobj.close()

# this function is used to output the result
def output(outputdir):
	sorteddl = sorted(doclength.items(), key=lambda d:d[0])
	sortedii = sorted(invertedindex.items(), key=lambda d:d[0])
	fobj = open(outputdir, "w")
	for tup in sorteddl:
		fobj.write("# " + str(tup[0]) + " " + str(tup[1]) + "\n")
	for tup in sortedii:
		s = str(sorted(tup[1].items(), key=lambda d:d[0]))
		fobj.write(tup[0] + " -> " + s[1:len(s) - 1] + "\n")
	fobj.close()

# this main function is used to run all the functions above
def main():
	read(sys.argv[1])
	output(sys.argv[2])

if __name__ == "__main__":
	main()
