(:problem deliver_1
  (:domain cargo)
  (:objects cargo_msy - cargo
            plane_atl - plane
            atl - airport
            msy - airport)
  (:initial (and (at cargo_msy atl)
                 (at plane_atl atl)))
  (:goal (at cargo_msy msy)))