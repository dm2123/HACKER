package com.example.hacker.voice

/**
 * HACKER Voice Personality (spec 11)
 * Natural, conversational responses in Hindi/Hinglish
 * Variations to avoid repetition
 */
object VoicePersonality {

    // Success responses
    private val successReplies = listOf(
        "Yes Boss, kaam complete ho gaya.",
        "Done Boss, sab theek hai.",
        "Yes Sir, ho gaya poora.",
        "Perfect Boss, ye lo complete kiya.",
        "Bilkul Boss, kar diya sab kaam.",
        "Haan Boss, samjh gaya aur kar diya.",
        "Yes Boss, torch on kar diya.",
        "Theek hai Boss, abhi khol diya.",
        "Ho gaya Boss, bass kar do aur enjoy karo."
    )

    private val successWithDetail = listOf(
        "Yes Boss, {action} kar diya. Ab aur kya chahiye?",
        "Ho gaya Boss — {action} poora. Kuch aur?",
        "Bilkul Boss, {action} complete. Badha sakte hain?",
        "Done Boss, {action} theek se. Next kya?",
        "Perfect Boss, {action} ho gaya. Aur help chahiye?"
    )

    // Failure responses
    private val failureReplies = listOf(
        "Boss, ye kaam complete nahi ho paya. Permission chahiye ho sakta hai.",
        "Maaf kijiye Boss, kuch gadbad ho gaya. Dobara try karun?",
        "Sorrrryy Boss, ye action possible nahi hai. Alternative bataun?",
        "Boss, ye device ke liye available nahi hai.",
        "Kuch issue aya Boss, try karna padega dobara."
    )

    // Confirmation requests
    private val confirmationReplies = listOf(
        "Boss, ye action sensitive hai. Confirmation chahiye? Haan ya na?",
        "Thoda risky hai ye. Sure ho aap? Continue karun?",
        "Ek second Boss — permission le lu pehle? Haan?",
        "Boss, permission chahiye isske liye. Approve karun?",
        "Ye mahtvapurn action hai Boss. Confirm karo?"
    )

    // Processing responses
    private val processingReplies = listOf(
        "Ho raha hai Boss, thoda patience...",
        "Samjh raha hoon Boss, wait karo...",
        "Process ho raha hai, ek second...",
        "Kaam chal raha hai Boss, ruko...",
        "Samajh gaya Boss, abhi complete karunga..."
    )

    // Greeting responses
    private val greetingReplies = listOf(
        "Hello Boss! Kya command chahiye aaj?",
        "Namaste Boss! Mein HACKER hoon, command do.",
        "Hey Boss! Ready hoon, bolo na kya karna hai.",
        "Subah ho ya raat, main humesha tayyar. Bolo Boss!",
        "Haan Boss, I'm listening. Kya ho sakta hai?"
    )

    // Help responses
    private val helpReplies = listOf(
        "Boss, ye kar sakte hain: torch, camera, WhatsApp, Spotify, Gemini AI, coding help, college assignment...",
        "Kya chahiye Boss? Torch on, app kholo, message bhejo, ya AI se poochho kuch bhi.",
        "Boht kuch kar sakta hoon — bolo torch? Camera? Message? Music? Code explanation?",
        "Boss, unlimited commands! Bolo torch, camera, Instagram, timer, notes, viva prep, ya kuch bhi."
    )

    // Error recovery
    private val errorRecoveryReplies = listOf(
        "Oops Boss! Kuch samajh nahi aaya. Dobara bolo?",
        "Sorry Boss, samjh nahi aaya. Clearer command do?",
        "Maaf karo Boss, ye nahi samajh aaya. Ek aur baar?",
        "Boss, thoda confuse hua. Dobara bata sakta hai?"
    )

    fun success(action: String? = null): String {
        return if (action != null) {
            successWithDetail.random().replace("{action}", action)
        } else {
            successReplies.random()
        }
    }

    fun failure(reason: String? = null): String {
        val base = failureReplies.random()
        return if (reason != null) "$base Reason: $reason" else base
    }

    fun confirmation(action: String): String {
        return confirmationReplies.random().replace("{action}", action)
    }

    fun processing(): String {
        return processingReplies.random()
    }

    fun greeting(): String {
        return greetingReplies.random()
    }

    fun help(): String {
        return helpReplies.random()
    }

    fun errorRecovery(): String {
        return errorRecoveryReplies.random()
    }

    // Specific action replies
    fun torchOn(): String = "Yes Boss, torch on kar diya. Andhera kahin nahi rahega!"
    
    fun torchOff(): String = "Done Boss, torch band kar diya. Bijli bachane ke liye!"
    
    fun cameraOpen(): String = "Haan Boss, camera khol gaya. Click karo!"
    
    fun appOpen(appName: String): String = "Yes Boss, $appName khol raha hoon. Wait karo thoda..."
    
    fun timerSet(minutes: Int): String = "Perfect Boss, $minutes minute ka timer set ho gaya. Alarm bajegi jab time pura ho!"
    
    fun volumeUp(): String = "Yes Boss, volume badha diya. Ab acha sound sunai dega!"
    
    fun volumeDown(): String = "Done Boss, volume dheema kar diya. Shanti se suno ab!"
    
    fun messageFormed(contact: String): String = "Yes Boss, $contact ko message form kar raha hoon. Bhej dunga now!"
    
    fun weatherInfo(city: String, temp: String): String = "$city me $temp degree hai Boss. Chalega na?"
    
    fun mathSolved(question: String, answer: String): String = "Boss, $question = $answer. Simple tha na!"
    
    fun assignmentHelp(topic: String): String = "Theek hai Boss, $topic ka structure tayyar kar diya. Dekh lo aur improve kar!"
    
    fun codeExplained(language: String): String = "Yes Boss, $language code explain kar diya. Samajh gaya?"
    
    fun weatherReport(condition: String): String = "Boss, bahar $condition hai. Bahar nikalne se pehle check kar!"

    // Context-aware responses
    fun contextualReply(command: String): String {
        return when {
            command.contains("thanks", ignoreCase = true) || command.contains("shukriya", ignoreCase = true) ->
                "Koi baat nahi Boss! Anytime help chahiye to bolo. Main ready hoon!"
            
            command.contains("sorry", ignoreCase = true) || command.contains("maafi", ignoreCase = true) ->
                "Boss, chhodo! Koi galti ho to dobara try karenge. Chalo ab kya karte hain?"
            
            command.contains("good", ignoreCase = true) || command.contains("badhiya", ignoreCase = true) ->
                "Shukriya Boss! Aapka satisfaction hi mera goal. Aur kuch chahiye?"
            
            command.contains("bye", ignoreCase = true) || command.contains("ciao", ignoreCase = true) ->
                "Thik hai Boss! Phir milenge jaldi. Bye bye! 👋"
            
            else -> success()
        }
    }
}
