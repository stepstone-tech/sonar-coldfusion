<cfset firstName = "Nico">

<!-- comment format
     multilines-->

    Hello <cfoutput>#firstName#</cfoutput>!
    This CFML tutorial was designed for
<cfif firstName eq "Nico">
        you!
<cfelse>
    <!-- Another comment format
     multilines

     -->

        the world to see.
</cfif>
