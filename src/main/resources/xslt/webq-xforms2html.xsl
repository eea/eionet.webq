<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf bf"
                xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <!-- If you need to overwrite some betterFORM XSLs (ie. change control layout or behaviour) then do it in this file.  -->

    <!-- Include all betterFORM XSLs -->
    <xsl:import href="xhtml.xsl"/>


    <!-- Overwrite betterForm InputDateAndTime template to change the default date pattern. -->
    <xsl:template name="InputDateAndTime">
        <xsl:param name="id"/>
        <xsl:param name="name"/>
        <xsl:param name="type"/>
        <xsl:param name="navindex"/>
        <xsl:param name="classes"/>

        <xsl:variable name="dataBfParams">
            <xsl:choose>
                <xsl:when test="exists(@data-bf-params) and string-length(@data-bf-params) &gt; 0">
                    <xsl:value-of select="@data-bf-params"/>,value:'<xsl:value-of select="bf:data/@bf:schema-value"/>'
                </xsl:when>
                <xsl:otherwise>date:'dd/MM/yyyy',value:'<xsl:value-of select="bf:data/@bf:schema-value"/>'
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <input id="{$id}-value"
                name="{$name}"
                type="{$type}"
                class="{$classes}"
                tabindex="{$navindex}"
                data-bf-params="{$dataBfParams}"
                placeholder="{xf:hint/text()}"
                value="{bf:data/text()}">
            <xsl:if test="bf:data/@bf:readonly='true'">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="@*" mode="copy-foreign-attributes"/>
        </input>
    </xsl:template>

    <!-- Overwrite betterFORM addDojoImport template to change the locale from 'en' to 'en-gb'. This changes the first day of week to Monday in calendar popup. -->
    <!-- We have to overwrite the whole template since the properties are hard-coded. -->

    <xsl:template name="addDojoImport">
        <!--
        todo: allow re-definition of dojoConfig: if a dojoConfig is present in the page use that instead of the code below.
        Or to be more precise - it should be possible to define your own package locations. Alternatively of course
        this template might be overwritten by a custom stylesheet. Which is better?


        -->
        <!-- todo: should we use explicit package locations and a baseUrl ? -->
        <!-- todo: use locale again -->
        <xsl:variable name="dojoConfig">
            has: {
            "dojo-firebug": <xsl:value-of select="$isDebugEnabled"/>,
            "dojo-debug-messages": <xsl:value-of select="$isDebugEnabled"/>
            },
            isDebug:<xsl:value-of select="$isDebugEnabled"/>,
            baseUrl: '<xsl:value-of select="concat($contextroot,$scriptPath)"/>',

            locale:'en-gb',
            extraLocale: ['en-gb'],

            parseOnLoad:false,
            async:true,

            packages: [
            'dojo',
            'dijit',
            'dojox',
            'bf'
            ],

            bf:{
            sessionkey: "<xsl:value-of select="$sessionKey"/>",
            contextroot:"<xsl:value-of select="$contextroot"/>",
            fluxPath:"<xsl:value-of select="concat($contextroot,'/Flux')"/>",
            useDOMFocusIN:<xsl:value-of select="$uses-DOMFocusIn"/>,
            useDOMFocusOUT:<xsl:value-of select="$uses-DOMFocusOut"/>,
            useXFSelect:<xsl:value-of select="$uses-xforms-select"/>,
            logEvents:<xsl:value-of select="$isDebugEnabled"/>,
            unloadingMessage:"<xsl:value-of select="$unloadingMessage"/>"
            }
        </xsl:variable>
        <xsl:text>
</xsl:text>
        <script type="text/javascript" src="{concat($contextroot,$scriptPath,'dojo/dojo.js')}">
            <xsl:attribute name="data-dojo-config"><xsl:value-of select="normalize-space($dojoConfig)"/></xsl:attribute>
        </script><xsl:text>
</xsl:text>
            <!--<script type="text/javascript" src="{concat($contextroot,$scriptPath,'bf/core.js')}">&#160;</script><xsl:text>-->
        <script type="text/javascript" src="{concat($contextroot,$scriptPath,'bf/bfRelease.js')}">&#160;</script><xsl:text>
</xsl:text>
        <xsl:if test="$isDebugEnabled">
            <script type="text/javascript" src="{concat($contextroot,$scriptPath,'bf/debug.js')}">&#160;</script><xsl:text>
</xsl:text>
        </xsl:if>

    </xsl:template>


</xsl:stylesheet>