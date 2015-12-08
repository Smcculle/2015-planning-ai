(:problem elevator_3
	(:domain elevator)
	(:objects 	p0 - passenger
                p1 - passenger
				f0 - floor
             	f1 - floor
                f2 - floor)
    (:initial 
    	(and (above f0 f1)
             (above f1 f2)
    		 (origin p0 f2)
             (origin p1 f0)
             (destin p1 f1)
    		 (destin p0 f0)
    		 (lift-at f0)))				    
	(:goal (and(served p0)
                (served p1))))



