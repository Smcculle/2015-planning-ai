(:problem elevator_2
	(:domain elevator)
	(:objects 	p0 - passenger
				f0 - floor
             	f1 - floor
                f2 - floor)
    (:initial 
    	(and (above f0 f1)
             (above f1 f2)
    		 (origin p0 f2)
    		 (destin p0 f0)
    		 (lift-at f0)))				    
	(:goal (served p0)))



