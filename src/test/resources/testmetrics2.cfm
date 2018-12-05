<cfset firstName = "Nico">

    Hello <cfoutput>#firstName#</cfoutput>!
    This CFML tutorial was designed for
<cfif firstName eq "Nico">
        you!
<cfelse>
        the world to see.
</cfif>
