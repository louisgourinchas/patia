(define (problem HANOI-1-0)
(:domain HANOI)
(:objects
    small medium big - ring
    s1 s2 s3 - spike
)

;initial config: rings stacked in order of size on s1
(:INIT
    (handempty)
    (bigger big medium) (bigger big small) (bigger medium small);sizes, small>medium>big
    (on small medium) (on medium big)
    (clear small)
    (clearspike s2) (clearspike s3)
    (onspike big s1)
 )

;goal: all rings stacked in order of size on s3
(:goal
    (AND (clearspike s1) (clearspike s2) (onspike big s3) (on small medium) (on medium big))
)
)
