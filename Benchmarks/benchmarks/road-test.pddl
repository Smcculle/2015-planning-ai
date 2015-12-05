(:problem road-test
  (:domain road-operators)
  (:objects jack - vehicle 
  			mark - vehicle
  			a - place
  			d - place
  			g - place)
  (:initial (and (at jack a) (at mark a)
	 (bridge a d) (bridge d a) (road d g) (road g d)))
  (:goal (and (at jack g) (at mark g))))