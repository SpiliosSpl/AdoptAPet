package com.spilios.adoptapet

object PetRepository {
    val pets = listOf(
        Pet(
            id = 1,
            name = "Ziva",
            age = 3,
            breed = "Cocker Spaniel",
            description = "Friendly and playful",
            sex = false,//false = female
            imageResId = R.drawable.ziva,
            health = "Vaccinations up to date, spayed / neutered."
        ),
        Pet(
            id = 2,
            name = "Polly",
            age = 5,
            breed = "African Grey Parrot",
            description = "Polly is a friendly African Grey Parrot with colorful feathers. She loves to mimic human speech and enjoys being around people.",
            sex = false,//false = female
            imageResId = R.drawable.parrot,
            health = "Healthy and active"
        ),
        Pet(
            id = 3,
            name = "Max",
            age = 2,
            breed = "Golden Retriever",
            description = "Loves walks",
            sex = true,
            imageResId = R.drawable.puppy,
            health = "Vaccinations up to date, spayed"
        ),
        Pet(
            id = 4,
            name = "Bella",
            age = 4,
            breed = "German Shepherd",
            description = "Energetic and loyal",
            sex = false,
            imageResId = R.drawable.german,
            health = "Vaccinations up to date, spayed"
        ),
        Pet(
            id = 5,
            name = "Babis",
            age = 3,
            breed = "Maine Coon",
            description = "Babis is a playful and affectionate Maine Coon cat. He has a fluffy coat and enjoys cuddling with his human companions.",
            sex = true,
            imageResId = R.drawable.cat,
            health = "Up to date on vaccinations and neutered"
        ),
        Pet(
            id = 6,
            name = "Buddy",
            age = 5,
            breed = "Labrador Retriever",
            description = "Buddy is a loyal and energetic Labrador Retriever. He enjoys playing fetch and going for long walks.",
            sex = true,
            imageResId = R.drawable.labrad,
            health = "Regular vet checkups and vaccinations"
        ),
        Pet(
            id = 7,
            name = "Nibbles",
            age = 1,
            breed = "Syrian Hamster",
            description = "Nibbles is a friendly and curious Syrian hamster. He loves exploring his cage and running on his wheel.",
            sex = true,
            imageResId = R.drawable.ham,
            health = "Kinda fat"
        )

    )
}
