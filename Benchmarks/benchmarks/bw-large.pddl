(:problem bw-large
  (:domain blocks)
  (:objects a - block
   			b - block
   			c - block
   			d - block
   			e - block
   			f - block
   			g - block
   			h - block
   			i - block
   			j - block
   			k - block)
  (:initial (and (on a table)
	 			 (on b a)
		 		 (on c b)
		 		 (clear c)
		 		 (on d table)
		 		 (on e d)
		 		 (on j e)
		 		 (on k j)
		 		 (clear k)
		 		 (on f table)
		 		 (on g f)
		 		 (on h g)
		 		 (on i h)
		 		 (clear i)))
  (:goal (and (on e j)
	      (on a e)
	      (on i d)
	      (on h i)
	      (on c k)
	      (on k g)
	      (on b c))))