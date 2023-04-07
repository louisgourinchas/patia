(define (problem HANOI-0-1)
(:domain HANOI)
(:objects
    a - ring
    s1 s2 - spike
)

;initial config: a is on s1
(:INIT
    (handempty)
    (clear a)
    (clearspike s2)
    (ontop a s1)
    (bottom a)
 )

;goal: a picked up, s1 empty
(:goal
    (AND (clearspike s1) (bottom a) (ontop a s2))
)
)
