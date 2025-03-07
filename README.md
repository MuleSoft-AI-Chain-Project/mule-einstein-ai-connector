# Mule4 Einstein AI Connector

## <img src="icon/icon.svg" width="6%" alt="banner"> [MuleSoft AI Chain - Einstein AI Connector](https://mac-project.ai/docs/einstein-ai)

MuleSoft Einstein AI Connector for Salesforce Einstein AI to interact with the _models API_ of the Salesforce platform and benefit from its trust layer and automation capabilities. It allows you to:
- **Leverage the Salesforce Trustlayer**
- **Ingest files into the Vector Database**
- **Build Prompt Templates in your workflow**
- **Perform adhoc RAG using the Vector Database**
- **Optimized usage in MuleSoft applications**

### Requirements

- The maximum supported version for Java SDK is JDK 17. You can use JDK 17 only for running your application.
- Compilation with Java SDK must be done with JDK 8.


### Installation
To use this connector, add the following dependency to your application's `pom.xml`:

```xml
    <dependency>
        <groupId>com.mulesoft.connectors</groupId>
        <artifactId>mule4-einstein-ai-connector</artifactId>
        <version>1.0.0</version>
        <classifier>mule-plugin</classifier>		
    </dependency>
```

### Installation (Open Source Version)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.mulesoft-ai-chain-project/mule4-einstein-ai-connector)](https://central.sonatype.com/artifact/io.github.mulesoft-ai-chain-project/mule4-einstein-ai-connector/overview)

#### Maven central dependency

```xml
    <dependency>
       <groupId>io.github.mulesoft-ai-chain-project</groupId>
       <artifactId>mule4-einstein-ai-connector</artifactId>
       <version>{version}</version>
       <classifier>mule-plugin</classifier>
    </dependency>
```

#### Build and install locally

To use this connector, first [build and install](https://mac-project.ai/docs/einstein-ai/getting-started) the connector into your local maven repository.
Then add the following dependency to your application's `pom.xml`:

```xml
    <dependency>
        <groupId>com.mulesoft.connectors</groupId>
        <artifactId>mule4-einstein-ai-connector</artifactId>
        <version>{version}</version>
        <classifier>mule-plugin</classifier>
    </dependency>
```

#### Private Anypoint Exchange

You can also make this connector available as an asset in your Anypoint Exchange.

This process will require you to build the connector as above, but additionally you will need
to make some changes to the `pom.xml`.  For this reason, we recommend you fork the repository.

Then, follow the MuleSoft [documentation](https://docs.mulesoft.com/exchange/to-publish-assets-maven) to modify and publish the asset.

### Documentation
- Check out MuleChain Einstein AI Connector Documentation on [mac-project.ai](https://mac-project.ai/docs/einstein-ai)
- Learn from the [Getting Started Playlist on YouTube](https://www.youtube.com/playlist?list=PLnuJGpEBF6ZAV1JfID1SRKN6OmGORvgv6)

---

#### Stay tuned!

- 🌐 **Website**: [mac-project.ai](https://mac-project.ai)
- 📺 **YouTube**: [@MuleSoft-MAC-Project](https://www.youtube.com/@MuleSoft-MAC-Project)
- 💼 **LinkedIn**: [MAC Project Group](https://lnkd.in/gW3eZrbF)



