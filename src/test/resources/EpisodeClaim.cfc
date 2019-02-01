<cfcomponent output="false" extends="com.remoteproxy">
    <cfsetting showdebugoutput="true">

    <cffunction name="hasEpisode" returntype="struct" access="remote" output="false" returnformat="JSON" hint="hint example">

        <cfargument name="episodeKey" type="numeric" required="true" />

        <cfset local.resultStruct = {} />
        <cfset local.resultStruct.has = false />
        <cfset local.resultStruct.result = "success">

        
        <!--testcomment-->
        
        
        <cftry>

            <cfset local.episodes = createObject("component", "packages.system.Episode.Episode.EpisodeGateway").init(application.dsn).getByAttributesQuery(fEpisodeKey=arguments.episodeKey) />

            <cfif local.episodes.recordCount GT 0>
                <cfset local.resultStruct.has = true />
            </cfif>

            <cfcatch type="any">
                <cfset local.resultStruct.result = "fail">
                <cfset local.resultStruct.errorStruct = super.createErrorJSON(cfcatch)>
            </cfcatch>

        </cftry>

        <cfreturn local.resultStruct />

    </cffunction>

</cfcomponent>
