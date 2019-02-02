package de.pondus

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.BuildAgent
import jetbrains.buildServer.serverSide.SQueuedBuild
import jetbrains.buildServer.serverSide.SRunningBuild
import jetbrains.buildServer.serverSide.buildDistribution.*
import java.util.*

/**
 * @author [Markus Moormann ](mailto:moormann@pondus.de)
 */
class BuildPerBranchPrecondition : StartBuildPrecondition {

    private val LOG = Logger.getInstance(BuildPerBranchPrecondition::class.java.name)
    private val DEFAULT_BRANCH_NAME = "master"
    private val DEFAULT_BRANCH = "<default>"

    private val ENABLED_SETTING = "build_per_branch_enabled"
    private val DEFAULT_BRANCH_SETTING = "build_per_branch_default_branch"

    override fun canStart(queuedBuildInfo: QueuedBuildInfo,
                          canBeStarted: Map<QueuedBuildInfo, BuildAgent>,
                          buildDistributorInput: BuildDistributorInput, b: Boolean): WaitReason? {
        if (queuedBuildInfo is SQueuedBuild) {
            val parameters = queuedBuildInfo.buildPromotionInfo.parameters
            val enabled = parameters.getOrDefault(ENABLED_SETTING, "false")

            if(!java.lang.Boolean.parseBoolean(enabled)) {
                return null
            }

            val defaultBranchName = parameters.getOrDefault(DEFAULT_BRANCH_SETTING, DEFAULT_BRANCH_NAME)
            val queuedBuildBranch = queuedBuildInfo.buildPromotion.branch?.name?.replace(DEFAULT_BRANCH, defaultBranchName)

            return if (existsRunningBuildWithSameBranch(defaultBranchName, queuedBuildInfo, queuedBuildBranch, buildDistributorInput.runningBuilds))
                SimpleWaitReason("branch $queuedBuildBranch is already building")
            else
                null
        }
        return null
    }

    private fun existsRunningBuildWithSameBranch(defaultBranchName: String,
                                                 queuedBuild: SQueuedBuild,
                                                 queuedBuildBranch: String?,
                                                 runningBuilds: Collection<RunningBuildInfo>): Boolean {
        if (queuedBuildBranch == null) {
            return false
        }
        val runningBranches = runningBuilds
                .asSequence()
                .filter { it is SRunningBuild }
                .map { it as SRunningBuild }
                .filter { it.buildTypeId == queuedBuild.buildTypeId }
                .filter { it.branch != null }
                .map { it.branch!!.displayName }
                .map { it.replace(DEFAULT_BRANCH, defaultBranchName) }
                .toList()
        if (LOG.isDebugEnabled) {
            LOG.debug("Build queue id ${queuedBuild.itemId}: ${queuedBuild.buildType.extendedFullName}")
            LOG.debug("queuedBranch: $queuedBuildBranch")
            LOG.debug("runningBranches: " + Arrays.toString(runningBranches.toTypedArray()))
        }
        return runningBranches.contains(queuedBuildBranch)
    }

}
