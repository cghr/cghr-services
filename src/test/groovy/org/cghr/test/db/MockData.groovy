package org.cghr.test.db

class MockData {


    def structure = [
            country: [id: 'int', name: 'varchar(20)', continent: 'varchar(20)'],
            user: [id: 'int', username: 'varchar(20)', password: 'varchar(20)', role: 'varchar(20)', status: 'varchar(20)'],
            authtoken: [id: 'int', token: 'varchar(255)', time: 'varchar(20)', expires: 'varchar(255)', username: 'varchar(20)', role: 'varchar(20)'],
            userlog: [id: 'int', username: 'varchar(20)', status: 'varchar(20)', time: 'varchar(20)', ipaddress: 'varchar(20)'],
            inbox: [id: 'int', message: 'varchar(100)', sender: 'varchar(20)', dwnStatus: 'varchar(20)', distList: 'varchar(20)', distStatus: 'varchar(20)', impStatus: 'varchar(20)'],
            outbox: [id: 'int', message: 'varchar(100)', recepient: 'varchar(20)', upldStatus: 'varchar(20)'],
            datachangelog: [id: 'int', log: 'text', status: 'varchar(20)']
    ]

    def sampleData = [
            country: [
                    [id: 1, name: 'india', continent: 'asia'],
                    [id: 2, name: 'pakistan', continent: 'asia'],
                    [id: 3, name: 'srilanka', continent: 'asia']
            ],
            user: [
                    [id: 1, username: 'user1', password: 'secret1', role: 'user', status: 'active'],
                    [id: 2, username: 'user2', password: 'secret2', role: 'user', status: 'active'],
                    [id: 3, username: 'user3', password: 'secret3', role: 'user', status: 'active'],
                    [id: 4, username: 'user4', password: 'secret4', role: 'manager', status: 'active'],
                    [id: 5, username: 'user5', password: 'secret5', role: 'admin', status: 'active']
            ],
            authtoken: [],
            userlog: [],
            inbox: [
                    [id: 1, message: 'file1.json', sender: 'admin', dwnStatus: null, distList: '1,2', distStatus: null, impStatus: null],
                    [id: 2, message: 'file2.json', sender: 'admin', dwnStatus: null, distList: '3,4', distStatus: null, impStatus: null]
            ],
            outbox: [
                    [id: 1, message: 'file1.json', recepient: 'admin', upldStatus: null],
                    [id: 2, message: 'file2.json', recepient: 'admin', upldStatus: null]
            ],
            datachangelog: [
                    [id: 1, log: '{"datastore":"country","data":{"id":1,"name":"india","continent":"asia"}}', status: null],
                    [id: 2, log: '{"datastore":"country","data":{"id":2,"name":"pakistan","continent":"asia"}}', status: null],
                    [id: 3, log: '{"datastore":"country","data":{"id":3,"name":"srilanka","continent":"asia"}}', status: null]

            ]
    ]
    def sampleDataUpdate = [
            country: [
                    [id: 1, name: 'india-update', continent: 'asia'],
                    [id: 2, name: 'pakistan-update', continent: 'asia'],
                    [id: 3, name: 'srilanka-update', continent: 'asia']
            ]
    ]
    List<Map> getFilteredSampleData(String datastore,List columns){


        List actualData=  this.sampleData.get(datastore)
        actualData.collect {
            it.subMap(columns)
        }

    }
}
