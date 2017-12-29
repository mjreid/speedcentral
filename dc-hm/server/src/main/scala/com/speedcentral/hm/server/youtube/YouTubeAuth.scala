package com.speedcentral.hm.server.youtube

import java.io.FileReader

import com.google.api.client.auth.oauth2.{Credential, StoredCredential}
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.{DataStore, FileDataStoreFactory}
import com.speedcentral.hm.server.config.YouTubeConfig

import scala.collection.JavaConverters._

class YouTubeAuth(
  youTubeConfig: YouTubeConfig
) {
  private val dataStoreName = "ytcreds"
  private val scopes = List(
    "https://www.googleapis.com/auth/youtube",
    "https://www.googleapis.com/auth/youtube.upload"
  )

  val transport = new NetHttpTransport()
  val jsonFactory = new JacksonFactory()

  def authorize(): Credential = {
    val clientSecretReader = new FileReader(youTubeConfig.credentialsFile.toFile)
    val clientSecrets = GoogleClientSecrets.load(jsonFactory, clientSecretReader)

    val fileDataStoreFactory = new FileDataStoreFactory(youTubeConfig.credentialsDirectory.resolve(dataStoreName).toFile)
    val dataStore = fileDataStoreFactory.getDataStore(dataStoreName).asInstanceOf[DataStore[StoredCredential]]

    val flow = new GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientSecrets, scopes.asJava)
      .setCredentialDataStore(dataStore)
      .build()

    val localReceiver = new LocalServerReceiver.Builder().setPort(youTubeConfig.receiverPort).build()

    new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user")
  }
}