(:problem elevator1
	(:domain elevator)
	(:objects 	p0 - passenger
				f0 - floor
             	f1 - floor)
    (:initial 
    	(and (above f0 f1)
    		 (origin p0 f1)
    		 (destin p0 f0)
    		 (lift-at f0)))				    
	(:goal (served p0)))



