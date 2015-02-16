package org.cghr.test.db

class MockData {


    def structure = [
            country: [id: 'int', name: 'varchar(20)', continent: 'varchar(20)'],
            user: [id: 'int', username: 'varchar(20)', password: 'varchar(20)', role: 'varchar(20)'],
            authtoken: [id: 'int', token: 'varchar(255)', time: 'varchar(20)', expires: 'varchar(255)', username: 'varchar(20)', role: 'varchar(20)'],
            userlog: [id: 'int', username: 'varchar(20)', status: 'varchar(20)', time: 'varchar(20)', ipaddress: 'varchar(20)'],
            inbox: [id: 'int auto_increment', datastore: 'varchar(100)', ref: 'varchar(20)', refId: 'varchar(20)', distList: 'varchar(20)', distStatus: 'varchar(20)', impStatus: 'varchar(20)'],
            outbox: [id: 'int', datastore: 'varchar(100)', ref: 'varchar(20)', refId: 'varchar(20)', recipient: 'varchar(20)', distList: 'varchar(20)', dwnStatus: 'varchar(20)'],
            datachangelog: [id: 'int', log: 'text', status: 'varchar(20)'],
            filechangelog: [id: 'int', filename: 'varchar(100)', filestore: 'varchar(100)', category: 'varchar(100)', status: 'varchar(20)'],
            memberImage: [memberId: 'int', consent: 'varchar(100)', photoId: 'varchar(100)', photo: 'varchar(100)'],
            sales:[name:'varchar(100)',total:'int',month:'int'],
            command:[id:'int',name: 'varchar(20)',status:'varchar(20)']
    ]

    def sampleData = [

            command: [
                    [id:1,name:'cleanup',status: null]
            ],
            sales: [
                    [name:'india',total:  100,month:  20],
                    [name:  'pakistan',total:  80,month:  10],
                    [name:  'srilanka',total:  40,month:  20]
            ],
            country: [
                    [id: 1, name: 'india', continent: 'asia'],
                    [id: 2, name: 'pakistan', continent: 'asia'],
                    [id: 3, name: 'srilanka', continent: 'asia']
            ],
            countryBatchData: [
                    [datastore: 'country', data: [id: '1', name: 'india', continent: 'asia']],
                    [datastore: 'country', data: [id: '2', name: 'pakistan', continent: 'asia']],
                    [datastore: 'country', data: [id: '3', name: 'srilanka', continent: 'asia']]

            ],
            user: [
                    [id: 1, username: 'user1', password: 'secret1', role: 'user'],
                    [id: 2, username: 'user2', password: 'secret2', role: 'user'],
                    [id: 3, username: 'user3', password: 'secret3', role: 'user'],
                    [id: 4, username: 'user4', password: 'secret4', role: 'manager'],
                    [id: 5, username: 'user5', password: 'secret5', role: 'admin']
            ],
            authtoken: [],
            userlog: [],
            inbox: [
                    [id: 1, datastore: 'country', ref: 'id', refId: '1', distList: '1,2', distStatus: null, impStatus: null],
                    [id: 2, datastore: 'country', ref: 'id', refId: '2', distList: '3,4', distStatus: null, impStatus: null]
            ],
            outbox: [
                    [id: 1, datastore: 'country', ref: 'id', refId: '1', recipient: '15', distList: null, dwnStatus: null],
                    [id: 2, datastore: 'country', ref: 'id', refId: '2', recipient: '16', distList: null, dwnStatus: null]
            ],
            datachangelog: [
                    [id: 1, log: '{"datastore":"country","data":{"id":1,"name":"india","continent":"asia"}}', status: null],
                    [id: 2, log: '{"datastore":"country","data":{"id":2,"name":"pakistan","continent":"asia"}}', status: null],
                    [id: 3, log: '{"datastore":"country","data":{"id":3,"name":"srilanka","continent":"asia"}}', status: null]

            ],
            filechangelog: [
                    [id: 1, filename: '151001001_consent.png', filestore: 'memberImage', category: 'memberConsent', status: null],
                    [id: 2, filename: '151001002_consent.png', filestore: 'memberImage', category: 'memberConsent', status: null],
                    [id: 3, filename: '151001003_consent.png', filestore: 'memberImage', category: 'memberConsent', status: null]
            ]
    ]
    def sampleDataUpdate = [
            country: [
                    [id: 1, name: 'india-update', continent: 'asia'],
                    [id: 2, name: 'pakistan-update', continent: 'asia'],
                    [id: 3, name: 'srilanka-update', continent: 'asia']
            ]
    ]

    List<Map> getFilteredSampleData(String datastore, List columns) {


        List actualData = this.sampleData.get(datastore)
        actualData.collect {
            it.subMap(columns)
        }

    }
}
