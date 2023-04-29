(define (problem HANOI-0-1)
(:domain HANOI)
(:objects
    a - ring
    s1 - spike
)

;initial config: a is on s1
(:INIT
    (handempty)
    (clear a)
    (onspike a s1)
 )

;goal: a picked up, s1 empty
(:goal
    (AND (clearspike s1) (holding a))
)
)
