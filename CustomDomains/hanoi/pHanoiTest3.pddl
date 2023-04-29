(define (problem HANOI-1-0)
(:domain HANOI)
(:objects
    small big - ring
    s1 s2 s3 - spike
)

;initial config: rings stacked in order of size on s1
(:INIT
    (handempty)
    (bigger big small)
    (on small big)
    (clear small)
    (clearspike s2) (clearspike s3)
    (onspike big s1)
 )

;goal: all rings stacked in order of size on s3
(:goal
    (AND (clearspike s1) (clearspike s2) (on small big) (onspike big s3))
)
)
