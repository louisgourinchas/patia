(define (problem HANOI-0-1)
(:domain HANOI)
(:objects
    a - ring
    s1 s2 - spike
)

;initial config: a is on s1, s2 is empty
(:INIT
    (handempty)
    (clear a)
    (clearspike s2)
    (onspike a s1)
 )

;goal: a picked up, s1 empty
(:goal
    (AND (clearspike s1) (onspike a s2))
)
)
