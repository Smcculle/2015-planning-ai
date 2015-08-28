(:problem deliver_return_2
  (:domain cargo)
  (:objects cargo_msy_1 - cargo
            cargo_msy_2 - cargo
            plane_atl - plane
            atl - airport
            msy - airport)
  (:initial (and (at cargo_msy_1 atl)
                 (at cargo_msy_2 atl)
                 (at plane_atl atl)))
  (:goal (and (at cargo_msy_1 msy)
              (at cargo_msy_2 msy)
              (at plane_atl atl))))