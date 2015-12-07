(:problem get-back-jack
  (:domain bulldozer)
  (:objects a - place
            b - place
            c - place
            d - place
            e - place
            f - place
            g - place
            jack - person
            bulldozer - vehicle)
  (:initial (and (at jack a) (at bulldozer e)
	 (road a b) (road b a)
	 (road a e) (road e a)
	 (road e b) (road b e)
	 (road a c) (road c a)
	 (road c b) (road b c)
	 (bridge b d) (bridge d b)
	 (bridge c f) (bridge f c)
	 (road d f) (road f d)
	 (road f g) (road g f)
	 (road d g) (road g d)))
  (:goal (and (at bulldozer g) (at jack a))))
