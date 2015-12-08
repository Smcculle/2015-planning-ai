
(:domain water
  (:requirements :strips :equality :typing :durative-actions)
(:types bowl hands)
 (:predicates 
 		(are_dirty ?h - hands)
 		(is_full ?b - bowl)
 		(clean ?b - bowl)
 		(washing ?h - hands)
		(draining ?b - bowl)
		(working ?h - hands)
 
 (:durative-action wash_hands
 	:parameters (?h - hands ?b - bowl)
 	:duration (= ?duration 2)
 	:condition (and (at start (is_full ?b))
 					(at start (are_dirty ?h))
 					(at start (not (working ?h)))
 					(at start (clean ?b))
 					(over all (washing ?h))
 					(over all (not (draining ?b)))
 			)
 	:effect (and (at start (not (clean ?b)))
 				 (at end (not (are_dirty ?h)))
 				 (at end (not (washing ?h)))
 			)
 )
 
 (:durative-action drain_water
 	:parameters (?b - bowl)
 	:duration (= ?duration 1)
 	:condition (and (at start (is_full ?b))
 					(at start (not (clean ?b)))
 					(at start (not (working ?h)))
 					(over all (draining ?b))
 			)
 	:effect (and (at start (not (is_full ?b)))
 				 (at end (clean ?b))
 				 (at end (not (draining ?b)))
 			)
 )

 (:durative-action draw_water
 	:parameters (?b - bowl)
 	:duration (= ?duration 2)
 	:condition (and (at start (not (is_full ?b)))
 					(at start (not (working ?h)))
 					(over all (filling ?b))
 			)
 	
 	:effect (and (at end (is_full ?b))
 				 (at end (not (filling ?b)))
 			)
 )

 (:durative-action do_work
 	:parameters (?h - hands)
 	:duration (= ?duration 3)
 	:condition (and (at start (not (are_dirty ?h)))
 					(at start (not (washing ?h)))
 					(at start (working ?h))
 					(over all (working ?h))
 					(over all (are_dirty ?h))
 			)
 	
 	:effect (and (at end (are_dirty ?h))
 				 (at end (not (working ?h)))
 			)
 )

)
