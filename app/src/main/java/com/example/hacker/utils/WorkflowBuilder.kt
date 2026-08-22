package com.example.hacker.utils

/**
 * HACKER 5.0 - Workflow step produced by the builder
 */
data class WorkflowStep(
    val order: Int,
    val description: String
)

data class Workflow(
    val title: String,
    val steps: List<WorkflowStep>
)

/**
 * Builds an ordered workflow plan from a parsed goal.
 */
object WorkflowBuilder {

    fun build(goal: Goal): Workflow {
        val steps = mutableListOf<WorkflowStep>()
        var order = 1

        steps.add(WorkflowStep(order++, "Understand goal: " + goal.rawText))

        when (goal.intent) {
            GoalIntent.STUDY_PLAN -> {
                steps.add(WorkflowStep(order++, "Break syllabus into daily chunks"))
                steps.add(WorkflowStep(order++, "Create revision timetable"))
                steps.add(WorkflowStep(order++, "Set daily study reminders"))
            }
            GoalIntent.ASSIGNMENT_WORKFLOW -> {
                steps.add(WorkflowStep(order++, "Collect requirements"))
                steps.add(WorkflowStep(order++, "Draft outline"))
                steps.add(WorkflowStep(order++, "Write and review draft"))
                steps.add(WorkflowStep(order++, "Finalize and submit"))
            }
            GoalIntent.CODING_SESSION -> {
                steps.add(WorkflowStep(order++, "Define problem statement"))
                steps.add(WorkflowStep(order++, "Set up project scaffold"))
                steps.add(WorkflowStep(order++, "Implement and test"))
            }
            GoalIntent.EXAM_PREP -> {
                steps.add(WorkflowStep(order++, "List exam topics"))
                steps.add(WorkflowStep(order++, "Prioritize weak areas"))
                steps.add(WorkflowStep(order++, "Practice mock tests"))
            }
            GoalIntent.PERSONAL_PROJECT -> {
                steps.add(WorkflowStep(order++, "Define scope"))
                steps.add(WorkflowStep(order++, "Plan milestones"))
                steps.add(WorkflowStep(order++, "Execute step by step"))
            }
            GoalIntent.UNKNOWN -> {
                steps.add(WorkflowStep(order++, "Ask user for more details"))
            }
        }

        val title = goal.intent.name.lowercase().replace('_', ' ')

        return Workflow(title = title, steps = steps)
    }
}
