(:domain road-operators
  (:action drive
	   :parameters (?vehicle - vehicle ?location1 - place ?location2 - place)
	   :precondition (and (at ?vehicle ?location1)
			      		  (road ?location1 ?location2))
	   :effect (and (at ?vehicle ?location2)
					(not (at ?vehicle ?location1))))
  (:action cross
	   :parameters (?vehicle - vehicle ?location1 - place ?location2 - place)
	   :precondition (and (at ?vehicle ?location1)
			      		  (bridge ?location1 ?location2))
	   :effect (and (at ?vehicle ?location2)
					(not (at ?vehicle ?location1)))))