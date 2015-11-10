(:problem deliver_return_1
  (:domain cargo)
  (:objects cargo_msy - cargo
            plane_atl - plane
            atl - airport
            msy - airport)
  (:initial (and (at cargo_msy atl)
                 (at plane_atl atl)))
  (:goal (and (at cargo_msy msy)
              (at plane_atl atl))))