(:problem tower-invert
  (:domain blocks)
  (:objects a - block
  			b - block
  			c - block)
  (:initial (and (on a b) 
  				 (on b c) 
  				 (on c table)
	 			 (clear a)))
  (:goal (and (on b c) (on c a))))