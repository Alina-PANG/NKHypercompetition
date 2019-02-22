# NKHypercompetition

Research project by 
- Jungpil Hahn, National University of Singapore
- Huseyin Tanriverdi, University of Texas at Austin
- Shi Ying Lim, National University of Singpaore

This package implements a hypercompetition model using the NK Fitness Landscapes framework.  

## Usage
- Download / clone repository
- create folder "out" for output files
- run "java app.Simulation [config file]"

## ToDo
- Incorporate ease of innovation
	- ~~innovation relates to how easy it is to connect to / adopt new resources (e.g., via SDKs and APIs)~~ DONE
	- ~~implement as parameter (innovation; double) that influences the likelihood of addResource (as opposed to mere search) in firms decisions~~ DONE
		- ~~currently it's implementing addResource given probability (innovation) then search~~ DONE
	- ~~should we think about how many resources to add at decision points?  -- Up to resourcesIncrement resources to add~~ DONE
- Incorporate data analytics capabilities
	- data analytics capabilities should make firms more likely to make better innovation (search) decisions
	- OR, it could be competitive intelligence -- i.e., better knowledge of the market (understanding the market's resource usage) or better innovation performance (knowledge of higher utility producing search -- i.e., exhaustive search rather than experiential; or change multiple resources at one time instead of one at a time -- i.e., long jump) 
- For equal 0s and 1s, don't adopt value randomly but the configuration that is owned by higher performing side 