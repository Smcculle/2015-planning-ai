(:problem bw-large-ds
  (:domain blocks)
  (:objects a - block
            b - block
            c - block
            d - block
            e - block
            f - block
            g - block
            h - block
            i -  block
	          j - block
            k - block
            l - block
            m - block
            n - block
            o - block
            p - block
            q - block
            r - block
            s - block)
  (:initial (and (on m table)
	               (on l m)
	               (on a l)
	               (clear a)
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
	               (clear i)
	               (on b table)
	               (on c b)
	               (on p c)
	               (on q p)
	               (on r q)
	               (on s r)
	               (clear s)))
  (:goal (and (on e j)
      	      (on a e)
      	      (on n a)
      	      (on s n)
      	      (on r s)
      	      (on q r)
      	      (on i d)
      	      (on h i)
      	      (on m h)
      	      (on o m)
      	      (on k g)
      	      (on p k)
      	      (on c p)
      	      (on b c)
      	      (on l b))))
