import os
import sys
import shutil
import glob
import subprocess
from lxml import etree
from StringIO import StringIO

val3dityexe =  '/Users/hugo/projects/val3dity/val3dity'

dErrors = {
  100: 'REPEATED_POINTS',
  110: 'RING_NOT_CLOSED',   
  120: 'RING_SELF_INTERSECT',
  200: 'SELF_INTERSECTION',  
  210: 'NON_PLANAR_SURFACE',                     
  220: 'INTERIOR_DISCONNECTED',
  230: 'HOLE_OUTSIDE',
  240: 'HOLES_ARE_NESTED',                      
  250: 'ORIENTATION_RINGS_SAME',
  300: 'NOT_VALID_2_MANIFOLD',
  301: 'SURFACE_NOT_CLOSED',                     
  302: 'DANGLING_FACES',                         
  303: 'FACE_ORIENTATION_INCORRECT_EDGE_USAGE',  
  304: 'FREE_FACES',                             
  305: 'SURFACE_SELF_INTERSECTS',                
  310: 'VERTICES_NOT_USED',                      
  320: 'SURFACE_NORMALS_WRONG_ORIENTATION',      
  400: 'SHELLS_FACE_ADJACENT',                   
  410: 'SHELL_INTERIOR_INTERSECT',               
  420: 'INNER_SHELL_OUTSIDE_OUTER',              
  430: 'INTERIOR_OF_SHELL_NOT_CONNECTED', 
}


def main():
  fin = open(sys.argv[1])
  construct_polys(fin)
  validate_polys(fin)
  remove_tmpolys()

def construct_polys(fin):
  print "Extracting the solids from the CityGML file"
  if not os.path.exists("tmpolys"):
    os.mkdir("tmpolys")
  else:
    shutil.rmtree("tmpolys")
    os.mkdir("tmpolys")
  os.chdir("..")
  os.system("java -classpath ./citygml4j-2.0ea-java6/lib/citygml4j.jar:. citygml2poly %s ./validate/tmpolys" % (fin.name))
  os.chdir("./validate/tmpolys")
  print "done.\n"

def remove_tmpolys():
  os.chdir("..")
  shutil.rmtree("tmpolys")


def validate_polys(fin):
  print "Validating each solid"
  # validate each building/shell
  dFiles = {}
  for f in os.listdir('.'):
    if f[-4:] == 'poly':
      i = (f.split('.poly')[0]).rfind('.')
      f1 = f[:i]
      if f1 not in dFiles:
        dFiles[f1] = [f]
      else:
        dFiles[f1].append(f)
  i = 0
  print "Number of solids in file:", len(dFiles)
  invalidsolids = 0
  xmlsolids = []
  exampleerrors = []
  for solidname in dFiles:
    # check if solid or multisurface in first file
    t = open(dFiles[solidname][0])
    t.readline()
    if t.readline().split()[1] == '0':
      multisurface = True
    else:
      multisurface = False
    t.close()
    
    # validate with val3dity
    str1 = val3dityexe + " -withids -xml " +  " ".join(dFiles[solidname])
    op = subprocess.Popen(str1.split(' '),
                          stdout=subprocess.PIPE, 
                          stderr=subprocess.PIPE)
    R = op.poll()
    if R:
       res = op.communicate()
       raise ValueError(res[1])
    o =  op.communicate()[0]
    if o.find('ERROR') != -1:
      invalidsolids += 1
      i = o.find('<errorCode>')
      while (i != -1):
        if exampleerrors.count(o[i+11:i+14]) == 0:
          exampleerrors.append(o[i+11:i+14])
        tmp = o[i+1:].find('<errorCode>')
        if tmp == -1:
          i = -1
        else:
          i = tmp + i + 1
    else: #-- no error detected, WARNING if MultiSurface!
      if multisurface == True:
        # print 'WARNING: MultiSurfce is actually a valid solid'
        s = []
        s.append("\t\t<ValidatorMessage>")
        s.append("\t\t\t<type>WARNING</type>")
        s.append("\t\t\t<explanation>MultiSurfaces form a valid Solid</explanation>")
        s.append("\t\t</ValidatorMessage>\n")
        o = "\n".join(s)
    o = '\t<Solid>\n\t\t<id>' + solidname + '</id>\n' + o + '\t</Solid>'
    xmlsolids.append(o)

  totalxml = []
  totalxml.append('<ValidatorContext>')
  totalxml.append('\t<inputFile>' + fin.name + '</inputFile>')
  totalxml.append("\n".join(xmlsolids))
  totalxml.append('</ValidatorContext>')
  s = fin.name
  s = s[:-4] + '_report.xml'
  fout = open(s, 'w')
  fout.write('\n'.join(totalxml))
  fout.close()
  print "Invalid solids: ", invalidsolids
  print "Errors present:"
  for each in exampleerrors:
    print each, dErrors[int(each)]
  print "Report of the validation:", s

if __name__ == "__main__":
    main()
  