(:problem deliver_return_3
  (:domain cargo)
  (:objects cargo_atl - cargo
            cargo_msy - cargo
            cargo_sfo - cargo
            plane_atl - plane
            plane_sfo - plane
            atl - airport
            msy - airport
            sfo - airport)
  (:initial (and (at cargo_atl msy)
                 (at cargo_msy sfo)
                 (at cargo_sfo atl)
                 (at plane_atl atl)
                 (at plane_sfo sfo)))
  (:goal (and (at cargo_atl atl)
              (at cargo_msy msy)
              (at cargo_sfo sfo)
              (at plane_atl atl)
              (at plane_sfo sfo))))