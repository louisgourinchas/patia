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
    (ondisk small)
    (on small big)
    (clear small)
    (clearspike s2) (clearspike s3)
    (ontop small s1)
    (bottom big)
 )

;goal: all rings stacked in order of size on s3
(:goal
    (AND (clearspike s1) (clearspike s2) (ontop small s3) (on small big))
)
)
