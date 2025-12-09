folder('Tools') {
    description('Folder for miscellaneous tools.')
}

job('Tools/clone-repository') {

    description('Job that clones a Git repository provided by the user.')

    parameters {
        stringParam(
            'GIT_REPOSITORY_URL',  
            '',                      
            'Git URL of the repository to clone'
        )
    }
    wrappers {
        preBuildCleanup()  
    }
    steps {
        shell("git clone \$GIT_REPOSITORY_URL")
    }

    triggers {} // Manually operates
}

job('Tools/SEED'){

    description('this is a seed job')

    parameters{
        stringParam(
            'GITHUB_NAME',
            '',
            'GitHub repository owner/repo_name'
        )
        stringParam(
            'DISPLAY_NAME',
            '',
            'Display name for the job'
        )
    }
    steps{
        dsl{
            external('job_dsl.groovy')
            lookupStrategy('SEED_JOB')
            removeAction('DELETE')
        }
    }
    triggers {}
}
if (binding.hasVariable('GITHUB_NAME') && binding.hasVariable('DISPLAY_NAME')) {

    job("${DISPLAY_NAME}") {

        description("Automatically generated job for ${GITHUB_NAME}")
        properties {
            githubProjectUrl("https://github.com/${GITHUB_NAME}")
        }
        //Source Control Management ==> how jenkins fetches code
        scm {
            git {
                remote {
                    url("https://github.com/${GITHUB_NAME}.git")
                }
                branch('*/main')
            }
        }
        wrappers {
            preBuildCleanup()
        }
        triggers {
            scm('* * * * *')   // Every minute
        }
        steps {
            shell("make fclean")
            shell("make")
            shell("make tests_run")
            shell("make clean")
        }
    }
}
