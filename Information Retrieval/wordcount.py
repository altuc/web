import numpy as np
import matplotlib.pyplot as plt
import sys
from math import log10, pow

words = {}  # dict to save the most frequent 25 words and their count, rank, probability, rank*probability
mwords = {} # dict to save the most frequent 25 additional words that start with the letter m and their count, rank, probability, rank*probability
wordcount = {} # dict to save the word and their count in the text
total = 0 # global counter to get the total number of words in the text
totalunique = 0 # global counter to get the total number of unique words in the text
proportion = 0.0 # proportion of the distinct words in the collection should be omitted

# this function is used to get the total number of words and unique words in the text
def count(filedir):
	global total
	global totalunique
	# read the processed text file
	fobj = open(filedir, "r")
	# process each line of the file
	for line in fobj:
		key = line.rstrip()
		# use word as the key. if the word already in the dict, the value for the word add 1, 
		# otherwise put the word in the dict with value 1
		if key in wordcount.keys():
			wordcount[key] += 1
		else:
			wordcount[key] = 1
		total += 1
	totalunique = len(wordcount)
	fobj.close()

# this function is used to get the count, rank, probability, rank*probability of the most frequent 25 words and 
# the most frequent 25 additional words that start with the letter m
def calculate():
	rank = 0
	# sort the wordcount dict in decreasing order according to the count of each word
	sortedwc = sorted(wordcount.items(), key=lambda d:d[1], reverse=True)
	for tup in sortedwc:
		key = tup[0]
		count = tup[1]
		rank += 1
		# save the list of count, rank, probability, rank*probability of the most frequent 25 words as value, 
		# word as key to the words dict
		if len(words) < 25:
		    words[key] = [count, rank, float(count)/total, float(rank*count)/total]
		# save the list of count, rank, probability, rank*probability of the most frequent 25 additional words 
		# that start with the letter m as value, word as key to the words dict
		if key[0] == 'm' and len(mwords) < 25 and (key not in words):
			mwords[key] = [count, rank, float(count)/total, float(rank*count)/total]
		if len(words) == 25 and len(mwords) == 25:
			break

# this function is used to get the proportion of the distinct words in the collection should be omitted
def omit():
	global proportion
	omits = 0
	for key in wordcount:
		# omit all words that occur fewer than five times
		if wordcount[key] < 5:
			omits += 1
	proportion = float(omits) / totalunique

# this function is used to determine the model parameters K and beta
def compute(filedir):
	count = 0; # counter to get the number of words occur in the text
	wordseen = {} # dict to save the distinct word in the text
	x = [] # list to save log10(number of words)
	y = [] # list to save log10(number of unique words)
	pairs = [] # list to save pairs of numbers: (number of words processed, number of unique words seen)
	# read the processed text file
	fobj = open(filedir, "r")
	# process each line of the file
	for line in fobj:
		key = line.rstrip()
		if key not in wordseen.keys():
			wordseen[key] = 1
		count += 1
		x.append(log10(count))
		y.append(log10(len(wordseen)))
		pair = (count, len(wordseen))
		pairs.append(pair)
	A = np.array([x, np.ones(count)]).T
	beta, kt = np.linalg.lstsq(A, y)[0]
	k = pow(10, kt)
	print "beta: " + str(beta)
	print "K: " + str(k)
	fobj.close()

# this function is used to output the result
def output():
	sortedwords = sorted(words.items(), key=lambda d:d[1][1])
	print "Top 25 most frequent words:"
	for tup in sortedwords:
		print "Word:" + tup[0].ljust(10) + "Times:" + str(tup[1][0]).ljust(10) + "Rank:" + str(tup[1][1]).ljust(8) + "Probability:" + str(tup[1][2]).ljust(22) + "Rank*Probability:" + str(tup[1][3])
	sortedmwords = sorted(mwords.items(), key=lambda d:d[1][1])
	print "Top 25 most frequent words start with the letter m:"
	for tup in sortedmwords:
		print "Word:" + tup[0].ljust(10) + "Times:" + str(tup[1][0]).ljust(10) + "Rank:" + str(tup[1][1]).ljust(8) + "Probability:" + str(tup[1][2]).ljust(22) + "Rank*Probability:" + str(tup[1][3])
	print "Total number of words: %d" % total
	print "Total number of unique words: %d" % totalunique
	print "The proportion of the distinct words in the collection omitted: %f" % proportion

# this function is used to draw the log-log plot
def plot():
	x = [] # list to save the rank of each word in the text
	y = [] # list to save the probability of each word in the text
	w = {} # dict to save the words in the text and their count, rank, probability, rank*probability
	rank = 0
	# sort the wordcount dict in decreasing order according to the count of each word
	sortedwc = sorted(wordcount.items(), key=lambda d:d[1], reverse=True)
	for tup in sortedwc:
		key = tup[0]
		count = tup[1]
		rank += 1
		w[key] = [count, rank, float(count)/total, float(rank*count)/total]
	sortedwords = sorted(w.items(), key=lambda d:d[1][2])
	for tup in sortedwords:
		x.append(tup[1][1])
		y.append(tup[1][2])
	plt.plot(x, y, color='m', marker='.', label='log-log plot')
	plt.xscale('log')
	plt.yscale('log')
	plt.xlabel('log(Rank)')
	plt.ylabel('log(Probability)')
	plt.legend()
	plt.show()

# this main function is used to run all the functions above
def main():
	count(sys.argv[1])
	calculate()
	omit()
	output()
	compute(sys.argv[1])
	plot()

if __name__ == "__main__":
    main()