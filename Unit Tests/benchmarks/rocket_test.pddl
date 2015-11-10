(:problem rocket_test
  (:domain rocket)
  (:objects Cargo - cargo
            Rocket - rocket
            NOLA - location
            London - location)
  (:initial (and (at Cargo NOLA)
                 (at Rocket NOLA)))
  (:goal (and (at Cargo London)
              (at Rocket NOLA))))