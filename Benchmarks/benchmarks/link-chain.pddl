(:domain link-chain-10
  (:action a1
           :parameters
           :effect (g1))

  (:action a2
           :parameters
           :precondition (g1)
           :effect (and (g2) (not (g1))))

  (:action a3
           :parameters
           :precondition (and (g2) (g1))
           :effect (and (g3) (g1) (not (g2))))

  (:action a4
           :parameters
           :precondition (and (g3) (g2) (g1))
           :effect (and (g4) (g2) (g1) (not (g3))))

  (:action a5
           :parameters
           :precondition (and (g4) (g3) (g2) (g1))
		   :effect (and (g5) (g3) (g2) (g1) (not (g4))))

  (:action a6
           :parameters
           :precondition (and (g5) (g4) (g3) (g2) (g1))
           :effect (and (g6) (g4) (g3) (g2) (g1) (not (g5))))

  (:action a7
           :parameters
           :precondition (and (g6) (g5) (g4) (g3) (g2) (g1))
           :effect (and (g7) (g5) (g4) (g3) (g2) (g1) (not (g6))))

  (:action a8
           :parameters
           :precondition (and (g7) (g6) (g5) (g4) (g3) (g2) (g1))
           :effect (and (g8) (g6) (g5) (g4) (g3) (g2) (g1) (not (g7))))

  (:action a9
           :parameters
           :precondition (and (g8) (g7) (g6) (g5) (g4) (g3) (g2) (g1))
           :effect (and (g9) (g7) (g6) (g5) (g4) (g3) (g2) (g1) (not (g8))))

  (:action a10
           :parameters
           :precondition (and (g9) (g8) (g7) (g6) (g5) (g4) (g3) (g2) (g1))
           :effect (and (g10) (g8) (g7) (g6) (g5) (g4) (g3) (g2) (g1) (not (g9)))))
