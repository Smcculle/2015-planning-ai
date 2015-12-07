(:domain link-chain-3

  (:action a1
           :parameters (?g0 - g ?g1 - g)
           :precondition(g0)
           :effect (g1) )

  (:action a2
           :parameters (?g1 - g ?g2 - g)
           :precondition (g1)
           :effect (and (g2) (not (g1))))

  (:action a3
           :parameters (?g1 - g ?g2 - g ?g3 - g)
           :precondition (and (g2) (g1))
           :effect (and (g3) (g1) (not (g2)))))
