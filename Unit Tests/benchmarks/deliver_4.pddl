(:problem deliver_4
  (:domain cargo)
  (:objects cargo_atl - cargo
            cargo_dfw - cargo
            cargo_msy - cargo
            cargo_sfo - cargo
            plane_atl - plane
            plane_sfo - plane
            atl - airport
            dfw - airport
            msy - airport
            sfo - airport)
  (:initial (and (at cargo_atl msy)
                 (at cargo_dfw msy)
                 (at cargo_msy sfo)
                 (at cargo_sfo atl)
                 (at plane_atl atl)
                 (at plane_sfo sfo)))
  (:goal (and (at cargo_atl atl)
              (at cargo_dfw dfw)
              (at cargo_msy msy)
              (at cargo_sfo sfo))))