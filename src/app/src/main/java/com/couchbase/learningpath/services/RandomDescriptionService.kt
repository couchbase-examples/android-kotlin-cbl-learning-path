package com.couchbase.learningpath.services

class RandomDescriptionService {
    fun randomDescription() : String {
        return paragraphs.random()
    }

    private val paragraphs = arrayOf(
        "You are doing the best you can, and that best results in good to yourself and to others. Do not nag yourself with a sense of failure. Get on your knees and ask for the blessings of the Lord;then stand on your feet and do what you are asked to do.",
        "I consider myself a stained-glass window. And this is how I live my life. Closing no doors and covering no windows; I am the multi-colored glass with light filtering through me, in many different shades. Allowing light to shed and fall into many many hues. My job is not to direct anything, but only to filter into many colors. My answer is destiny and my guide is joy. And there you have me.",
        "I have frequently seen people become neurotic when they content themselves with inadequate or wrong answers to the questions of life. They seek position, marriage, reputation, outward success of money, and remain unhappy and neurotic even when they have attained what they were seeking. Such people are usually confined within too narrow a spiritual horizon. Their life has not sufficient content, sufficient meaning. If they are enabled to develop into more spacious personalities, the neurosis generally disappears.",
        "You can't become a decent horseman until you fall off and get up again, a good number of times. There's life in a nutshell",
        "Have regular hours for work and play; make each day both useful and pleasant, and prove that you understand the worth of time by employing it well. Then youth will bring few regrets, and life will become a beautiful success.",
        "You were put on this earth to achieve your greatest self, to live out your purpose, and to do it courageously.",
        "Fear not the path of Truth for the lack of People walking on it.",
        "The Dingo Fence in Australia is longer than the distance between New York City to London.",
        "Because there are more molecules of air in one breath than there are breaths in the atmosphere, every breath you take likely contains at least one molecule of Newton’s last breath.",
        "If sound could travel through space, the noise that the sun would be the equivalent to a train horn from 1 meter away.",
        "Canada's forests make up nearly 9% of the world’s total forest area.",
        "Oxford University is older than the Aztec Empire",
        "Twinkies only have a shelf life of 45 days.",
        "Jupiter has over 70 moons.",
        "On average, the closest planet to Earth is Mercury. On average, the closest planet to Pluto is also Mercury.",
        "The Federal Emergency Management Agency (FEMA) uses Waffle House Restaurants being open or closed as one way to determine the effect of a storm and the likely scale of assistance required for disaster recovery.",
        "A giraffe and a human have the same number of bones in their necks.",
        "Camels store water their bloodstream, not their hump which is fatty tissue.",
        "Most of the visible stars you see in the night sky are binary stars - two stars orbiting each other.",
        "The eighth power of a number is a zenzizenzizenzic.",
        "The Brontosaurus never existed."
    )
}