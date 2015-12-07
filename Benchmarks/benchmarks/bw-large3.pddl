(:problem bw-large-as
  (:domain blocks)
  (:objects a - block
            b - block
            c - block
            d - block
            e - block
            f - block
            g - block
            h - block
            i - block)
  (:initial (and (on a table)
	               (on b a)
	               (on c b)
	               (clear c)
	               (on d table)
	               (on e d)
                 (clear e)
                 (on f table)
	               (on g f)
	               (on h g)
	               (on i h)
	               (clear i)))
  (:goal (and (on a e)
	            (on i d)
	            (on h i)
	            (on c g)
	            (on b c))))
