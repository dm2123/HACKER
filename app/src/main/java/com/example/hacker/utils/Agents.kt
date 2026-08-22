package com.example.hacker.utils

/**
 * HACKER 5.0 - Specialized agent types
 */
enum class AgentType {
    PERSONAL_ASSISTANT,
    PHONE_CONTROL,
    STUDY_AGENT,
    CODING_AGENT,
    ASSIGNMENT_AGENT,
    DOCUMENT_AGENT,
    PLANNER_AGENT,
    AUTOMATION_AGENT
}

data class Agent(
    val id: AgentType,
    val name: String,
    val description: String,
    val capabilities: List<String>
)

/**
 * Registry of the 8 HACKER agents with intent-based routing.
 */
object AgentRegistry {

    private val agents: List<Agent> = listOf(
        Agent(
            id = AgentType.PERSONAL_ASSISTANT,
            name = "Personal Assistant",
            description = "General chat and Q&A",
            capabilities = listOf("chat", "answer", "translate")
        ),
        Agent(
            id = AgentType.PHONE_CONTROL,
            name = "Phone Control",
            description = "Torch, volume, calls, SMS",
            capabilities = listOf("torch", "volume", "call", "sms")
        ),
        Agent(
            id = AgentType.STUDY_AGENT,
            name = "Study Agent",
            description = "Study plans and revision schedules",
            capabilities = listOf("study plan", "revision", "pomodoro")
        ),
        Agent(
            id = AgentType.CODING_AGENT,
            name = "Coding Agent",
            description = "Code help, debugging, snippets",
            capabilities = listOf("code", "debug", "explain")
        ),
        Agent(
            id = AgentType.ASSIGNMENT_AGENT,
            name = "Assignment Agent",
            description = "Assignment breakdown and tracking",
            capabilities = listOf("assignment", "homework", "deadline")
        ),
        Agent(
            id = AgentType.DOCUMENT_AGENT,
            name = "Document Agent",
            description = "Notes, summaries, formatting",
            capabilities = listOf("summarize", "notes", "format")
        ),
        Agent(
            id = AgentType.PLANNER_AGENT,
            name = "Planner Agent",
            description = "Daily planning and reminders",
            capabilities = listOf("plan day", "schedule", "reminder")
        ),
        Agent(
            id = AgentType.AUTOMATION_AGENT,
            name = "Automation Agent",
            description = "Multi-step workflows",
            capabilities = listOf("workflow", "automate", "routine")
        )
    )

    fun getAll(): List<Agent> = agents

    fun route(intent: GoalIntent): Agent {
        return when (intent) {
            GoalIntent.STUDY_PLAN -> find(AgentType.STUDY_AGENT)
            GoalIntent.ASSIGNMENT_WORKFLOW -> find(AgentType.ASSIGNMENT_AGENT)
            GoalIntent.CODING_SESSION -> find(AgentType.CODING_AGENT)
            GoalIntent.EXAM_PREP -> find(AgentType.PLANNER_AGENT)
            GoalIntent.PERSONAL_PROJECT -> find(AgentType.PLANNER_AGENT)
            GoalIntent.UNKNOWN -> find(AgentType.PERSONAL_ASSISTANT)
        }
    }

    private fun find(type: AgentType): Agent {
        for (agent in agents) {
            if (agent.id == type) {
                return agent
            }
        }
        return agents[0]
    }
}
