// Copyright (c) 2015, the Dart project authors.  Please see the AUTHORS file
// for details. All rights reserved. Use of this source code is governed by a
// BSD-style license that can be found in the LICENSE file.

library analyzer.src.plugin.command_line_plugin;

import 'package:analyzer/plugin/command_line.dart';
import 'package:plugin/plugin.dart';

/**
 * A plugin that defines the extension points and extensions that are defined by
 * command-line applications using the analysis engine.
 */
class CommandLinePlugin implements Plugin {
  /**
   * The simple identifier of the extension point that allows plugins to
   * register new parser contributors.
   */
  static const String PARSER_CONTRIBUTOR_EXTENSION_POINT = 'parserContributor';

  /**
   * The simple identifier of the extension point that allows plugins to
   * register new result processors.
   */
  static const String RESULT_PROCESSOR_EXTENSION_POINT = 'resultProcessor';

  /**
   * The unique identifier of this plugin.
   */
  static const String UNIQUE_IDENTIFIER = 'command_line.core';

  /**
   * The extension point that allows plugins to register new parser
   * contributors.
   */
  ExtensionPoint parserContributorExtensionPoint;

  /**
   * The extension point that allows plugins to register new result processors.
   */
  ExtensionPoint resultProcessorExtensionPoint;

  /**
   * Initialize a newly created plugin.
   */
  CommandLinePlugin();

  /**
   * Return a list containing all of the parser contributors that were
   * contributed.
   */
  List<ArgParserContributor> get parserContributors =>
      parserContributorExtensionPoint.extensions;

  /**
   * Return a list containing all of the result processors that were
   * contributed.
   */
  List<ArgParserContributor> get resultProcessors =>
      resultProcessorExtensionPoint.extensions;

  @override
  String get uniqueIdentifier => UNIQUE_IDENTIFIER;

  @override
  void registerExtensionPoints(RegisterExtensionPoint registerExtensionPoint) {
    parserContributorExtensionPoint = registerExtensionPoint(
        PARSER_CONTRIBUTOR_EXTENSION_POINT,
        _validateParserContributorExtension);
    resultProcessorExtensionPoint = registerExtensionPoint(
        RESULT_PROCESSOR_EXTENSION_POINT, _validateResultProcessorExtension);
  }

  @override
  void registerExtensions(RegisterExtension registerExtension) {
    // There are no default extensions.
  }

  /**
   * Validate the given extension by throwing an [ExtensionError] if it is not a
   * valid parser contributor.
   */
  void _validateParserContributorExtension(Object extension) {
    if (extension is! ArgParserContributor) {
      String id = parserContributorExtensionPoint.uniqueIdentifier;
      throw new ExtensionError(
          'Extensions to $id must be an ArgParserContributor');
    }
  }

  /**
   * Validate the given extension by throwing an [ExtensionError] if it is not a
   * valid result processor.
   */
  void _validateResultProcessorExtension(Object extension) {
    if (extension is! ArgResultsProcessor) {
      String id = resultProcessorExtensionPoint.uniqueIdentifier;
      throw new ExtensionError(
          'Extensions to $id must be an ArgResultsProcessor');
    }
  }
}
