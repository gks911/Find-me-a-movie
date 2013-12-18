import urllib
import json
import time

def extractmoviename(string):

      start_pos = string.find('::')
      end_pos = string.find('(')
      name = string[start_pos + 2 : end_pos - 1]
      if name[-3:] == "The":
            newname = "The " + name[:-5]
      elif name[-1] == "A":
            newname = "A " + name[:-3]
      elif name[-2:] == "An":
            newname = "An " + name[:-4]
      else:
            newname = name
      return newname

def readfile():
      movienames = []
      with open("movies.dat", "r") as fr:
            for line in fr:           
                  movie = extractmoviename(line)
                  movienames.append(movie)
            return movienames
      fr.close()

movienames = readfile()

jstr = []
i = 0
for moviename in movienames:

      j = ""
      
      url = 'https://www.googleapis.com/freebase/v1/mqlread?query={"type":"/film/film","name":"' + moviename + '","genre":[],"directed_by":[],"starring":[{"actor":[]}]}&key=AIzaSyDske8En0zCLrvsDxKiZ3seGc-YfQnEWyQ'
      #print "Trying to fetch:" + moviename
      
      j = urllib.urlopen(url).read()
      
      if(len(j) > 20 and ("error" not in j)):
            jstr.append(j)
            #print "Successfully fetched : " + moviename
            #print "------------------------------------------------"


            f = open("moviesdata.txt", "a")
            
            obj = json.loads(j)
            try:

                  f.write(obj[u'result'][u'name'] + "|")

                  for each in obj[u'result'][u'genre']:
                        if not each == obj[u'result'][u'genre'][-1]:
                              f.write(each.strip() + ",")
                        else:
                              f.write(each.strip() + "|")

                  for each in obj[u'result'][u'starring']:
                        if not each == obj[u'result'][u'starring'][-1]:
                              f.write(each[u'actor'][0].strip() + ",")
                        else:
                              f.write(each[u'actor'][0].strip() + "|")

                  for each in obj[u'result'][u'directed_by']:
                        if not each == obj[u'result'][u'directed_by'][-1]:
                              f.write(each.strip() + ",")
                        else:
                              f.write(each.strip() + "\n")
                  
            except BaseException:
                  pass

            f.close()
