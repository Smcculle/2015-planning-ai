(:problem bw-large-cs
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
            k - block
            l - block
            m - block
            n - block
            o - block)
  (:initial (and (on m table)
	            (on l m)
	            (on a l)
	            (on b a)
	            (on c b)
	            (clear c)
	            (on o table)
	            (on n o)
	            (on d n)
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
	            (on n a)
	            (on i d)
	            (on h i)
	            (on m h)
	            (on o m)
	            (on k g)
	            (on c k)
	            (on b c)
	            (on l b))))
