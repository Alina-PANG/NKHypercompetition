# **conf** folder

- Configuration files (experimental setup) should be stored here.  
- Run by invoking ">> java app.Simuation conf/testing.conf"


# File Format
```
N=<value of N>
numResources=<number of resources to endow each firm at init>
numFirms=<number of firms in ecosystem>
influenceMatrixFile=<name of the influence matrix file; in the inf folder>
iterations=<number of simulated time ticks to run>
adaptation={search resources}
outfile=<name of output file; if omitted, STDOUT will be used>
```

# Default Values
```
N=20
numResources=4
numFirms=10
influenceMatrixFile=matrix12
iterations=100
adaptation=resources
outfile=output.txt
```