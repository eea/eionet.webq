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

    <!-- ### this parameter is used when the Adapter wants to specify the CSS to use ### -->
    <xsl:param name="webq-css" select="''"/>

    <xsl:param name="envelope" select="''"/>
    <xsl:param name="instance" select="''"/>
    <xsl:variable name="cdrUrl">
        <xsl:choose>
            <xsl:when test="string-length($envelope) &gt; 0"><xsl:value-of select="concat(substring-before($envelope, '//'), '//', substring-before(substring-after($envelope, '//'), '/'))"/></xsl:when>
            <xsl:when test="string-length($instance) &gt; 0"><xsl:value-of select="concat(substring-before($instance, '//'), '//', substring-before(substring-after($instance, '//'), '/'))"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$baseURI"/></xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

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

    <!--  Overwrite BF template for displaying custom styles from "getEionetCss" template. -->
    <xsl:template name="getLinkAndStyle">
        <xsl:call-template name="getEionetCss"/><xsl:text>
</xsl:text><xsl:for-each select="link">
            <xsl:element name="{local-name()}">
                <xsl:copy-of select="@*" />
            </xsl:element>
        </xsl:for-each><xsl:text>
</xsl:text>

    </xsl:template>
    <!-- Add EIONET css links to webform -->
    <xsl:template name="getEionetCss">

        <meta name="Publisher" content="EEA, The European Environment Agency" />
        <xsl:choose>
            <!-- if the 'css-file' parameter has been set this takes precedence -->
            <xsl:when test="string-length($webq-css) > 0">
                <link rel="stylesheet" type="text/css" href="{$webq-css}" media="screen"/>
            </xsl:when>
            <xsl:otherwise>
                <link type="text/css" rel="stylesheet" media="screen" href="//www.eionet.europa.eu/styles/eionet2007/screen.css" title="Eionet 2007 style" />
            </xsl:otherwise>
        </xsl:choose>
        <!-- EIONET 2007 styles -->
        <link rel="stylesheet" type="text/css" href="//www.eionet.europa.eu/styles/eionet2007/print.css" media="print" />
        <link rel="stylesheet" type="text/css" href="//www.eionet.europa.eu/styles/eionet2007/handheld.css" media="handheld" />
        <style type="text/css">
            #bfLoading{
                position: fixed;
                top:5px;
            }
            #betterformMessageToaster{
                position: fixed;
                top:5px;
            }
        </style>
    </xsl:template>

    <xsl:template match="div[@id='workarea']">
        <xsl:variable name="cdrName">
            <xsl:choose>
                <xsl:when test="contains($cdrUrl , 'bdr.')">BDR</xsl:when>
                <xsl:when test="contains($cdrUrl , 'bdr-test.')">BDR-TEST</xsl:when>
                <xsl:when test="contains($cdrUrl , 'cdrtest.')">CDRTEST</xsl:when>
                <xsl:when test="contains($cdrUrl , 'cdr.')">CDR</xsl:when>
                <xsl:otherwise>WebQ</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <div id="container">
            <div id="toolribbon">
                <div id="lefttools">
                    <a id="eealink" href="http://www.eea.europa.eu/">EEA</a>
                    <a id="ewlink" href="http://www.ewindows.eu.org/">EnviroWindows</a>
                </div>
                <div id="righttools">
                    <!-- FIXME is logout link needed?
                    <span>
                        <a id="logoutlink">
                            <xsl:attribute name="href">WebQServlet?mode=logout&amp;logoutUrl=<xsl:value-of select="$baseURI"/>../loggedout</xsl:attribute>
                            Logout</a>
                    </span>
                    -->
                    <a id="printlink" title="Print this page" href="javascript:this.print();"><span>Print</span></a>
                </div>
            </div> <!-- toolribbon -->

            <div id="pagehead">
                <a href="/" accesskey="1"><img src="/images/eea-print-logo.gif" alt="Logo" id="logo" /></a>
                <div id="networktitle">Eionet</div>
                <div id="sitetitle">European Environment Information and Observation Network</div>
                <div id="sitetagline">Web questionnaires</div>
            </div>  <!-- page head -->

            <div id="menuribbon"></div>

            <div class="breadcrumbtrail">
                <div class="breadcrumbhead">You are here:</div>
                <div class="breadcrumbitem eionetaccronym"><a href="http://www.eionet.europa.eu/">Eionet</a></div>
                <div class="breadcrumbitem">
                    <xsl:element name="a">
                        <xsl:attribute name="href"><xsl:value-of select="$cdrUrl"/></xsl:attribute>
                        <xsl:value-of select="$cdrName"/>
                    </xsl:element>
                </div>
				<xsl:if test="string-length($envelope) &gt; 0">
					<div class="breadcrumbitem">
						<xsl:element name="a">
							<xsl:attribute name="href"><xsl:value-of select="$envelope"/></xsl:attribute>
							Envelope
						</xsl:element>
					</div>
				</xsl:if>
                <div class="breadcrumbitemlast">WebForm</div>
                <div class="breadcrumbtail"></div>
            </div>
            <div id="workarea">
            <xsl:apply-templates/>
            </div>
        </div> <!-- container -->
        <div id="pagefoot">
            <p><a href="https://www.eea.europa.eu/"><b>European Environment Agency</b></a>
            <br/>Kgs. Nytorv 6, DK-1050 Copenhagen K, Denmark</p>
        </div>
        <div style="display:none">baseURI: <xsl:value-of select="$baseURI"/></div>
        <div style="display:none">envelope: <xsl:value-of select="$envelope"/></div>
        <div style="display:none">instance: <xsl:value-of select="$instance"/></div>
    </xsl:template>

    <!-- Overwrite bf template for fixing inline javascript - do not escape it. -->
    <xsl:template name="copyInlineScript">
        <!-- copy inline javascript -->
        <xsl:for-each select="script">
            <script xmlns="http://www.w3.org/1999/xhtml">
                <xsl:attribute name="type">
                    <xsl:value-of select="@type"/>
                </xsl:attribute>
                <xsl:if test="exists(@src)">
                    <xsl:attribute name="src">
                        <xsl:value-of select="@src"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="exists(@defer)">
                    <xsl:attribute name="defer">
                        <xsl:value-of select="@defer"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="not(exists(@src))">
                    <!--<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>-->
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    <!--<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>               -->
                </xsl:if>
            </script>
                <xsl:text>
</xsl:text>
        </xsl:for-each>
    </xsl:template>


    <!-- from repeat-ui.xsl -->
    <xsl:template
            match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:upload|xf:trigger|xf:submit"
            mode="repeated-full-prototype"
            priority="20">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="label-classes"><xsl:call-template name="assemble-label-classes"/></xsl:variable>

        <xsl:element name="span">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="data-bf-class" select="$control-classes"/>

            <xsl:if test="exists(@mediatype)">
                <xsl:attribute name="mediatype" select="@mediatype"/>
            </xsl:if>
            <!-- WEBQ fixed label -->
            <xsl:if test="local-name() != 'trigger' and local-name() != 'submit'">
                <label for="{$id}-value" id="{$id}-label" class="{$label-classes}">
                    <xsl:call-template name="create-label">
                        <xsl:with-param name="label-elements" select="xf:label"/>
                    </xsl:call-template>
                </label>
            </xsl:if>

            <!--<xsl:apply-templates select="xf:alert"/>-->
            <span class="widgetContainer">
                <xsl:call-template name="buildControl"/>
                <xsl:apply-templates select="xf:alert"/>
                <xsl:apply-templates select="xf:hint"/>
                <xsl:apply-templates select="xf:help"/>
            </span>
        </xsl:element>
    </xsl:template>


    <!-- from html-form-controls.xsl -->
    <!-- Put hint into title attribute instead of placeholder. -->
    <xsl:template name="InputDefault">
        <xsl:param name="id"/>
        <xsl:param name="name"/>
        <xsl:param name="navindex"/>
        <xsl:param name="classes"/>
        <input id="{$id}-value"
                name="{$name}"
                type="text"
                class="{$classes}"
                tabindex="{$navindex}"
                title="{xf:hint}"
                value="{bf:data/text()}">
            <xsl:if test="bf:data/@bf:readonly='true'">
                <xsl:attribute name="disabled">disabled</xsl:attribute>
            </xsl:if>
            <xsl:for-each select="@*[not(local-name(.) = 'ref' or local-name(.) = 'style' or local-name(.) = 'id' or local-name(.) = 'class' or local-name(.) = 'title')]">
                <xsl:copy/>
            </xsl:for-each>
        </input>
    </xsl:template>

</xsl:stylesheet>